package tech.ravon.service.unres;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import tech.ravon.model.unres.UniRes;
import tech.ravon.model.unres.UrcOperation;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 完整 URC Service，包括：
 * - URC生成（10/15/24位）
 * - 批次码计算
 * - 流水码自动生成
 * - 认证码生成与更新
 * - 操作记录追溯
 * - 产品/原材料管理（父子关系）
 * - 多语言支持
 * - 查询产品及一层子原材料
 */
@Service
public class UniResService {

    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final AtomicInteger serialCounter = new AtomicInteger(0);

    // -------------------------- URC生成 & 校验 --------------------------

    private int charToInt(char c) {
        return CHARS.indexOf(Character.toUpperCase(c));
    }

    private char intToChar(int i) {
        return CHARS.charAt(i % 36);
    }

    private char calcCheck(String code, int[] weights) {
        int sum = 0;
        for (int i = 0; i < weights.length; i++) sum += charToInt(code.charAt(i)) * weights[i];
        return intToChar((36 - sum % 36) % 36);
    }

    public char calcCheck10(String first9) {
        return calcCheck(first9, new int[]{1, 3, 1, 3, 1, 3, 1, 3, 1});
    }

    public char calcCheck15(String first14) {
        return calcCheck(first14, new int[]{3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1});
    }

    private String generateAuthCode(String input) {
        Argon2 argon2 = Argon2Factory.create();
        try {
            String hash = argon2.hash(2, 65536, 1, input.getBytes());
            byte[] bytes = hash.getBytes();
            StringBuilder auth = new StringBuilder();
            for (int i = 0; i < 4 && i < bytes.length; i++) {
                auth.append(intToChar(bytes[i] & 0xFF % 36));
            }
            return auth.toString().toUpperCase();
        } finally {
            argon2.wipeArray(input.getBytes());
        }
    }

    public String generateAuthCodeFirst(String first20) {
        return generateAuthCode(first20);
    }

    public String updateAuthCode(String prevAuth, String factoryId, String operationId) {
        return generateAuthCode(prevAuth + factoryId + operationId);
    }

    // -------------------------- 批次码 --------------------------

    public String calculateBatchCode(int year, int subBatch) {
        WeekFields wf = WeekFields.of(Locale.getDefault());
        int week = Instant.now().get(wf.weekOfWeekBasedYear());
        int batchValue = (year % 100) * 1000 + week * 32 + (subBatch - 1);
        StringBuilder batchCode = new StringBuilder();
        while (batchValue > 0) {
            batchCode.insert(0, intToChar(batchValue % 36));
            batchValue /= 36;
        }
        while (batchCode.length() < 4) batchCode.insert(0, '0');
        return batchCode.toString();
    }

    // -------------------------- URC生成 --------------------------

    public UniRes generateURC(String countryCode, String factoryCode, String productCode,
                              Integer subBatch, String type, String name, String description) {

        String first9 = countryCode + factoryCode + productCode;
        char check10 = calcCheck10(first9);
        if ("Basic".equalsIgnoreCase(type)) {
            return new UniRes(null, first9 + check10, "Basic", name, description, new ArrayList<>());
        }

        String batch = calculateBatchCode(LocalDate.now().getYear(), subBatch != null ? subBatch : 1);
        String first14 = first9 + batch;
        char check15 = calcCheck15(first14);
        if ("Plus".equalsIgnoreCase(type)) {
            return new UniRes(null, first14 + check15, "Plus", name, description, new ArrayList<>());
        }

        String serialCode = Integer.toString(serialCounter.getAndIncrement(), 36).toUpperCase();
        while (serialCode.length() < 5) serialCode = "0" + serialCode;
        String first20 = first14 + serialCode;
        String authCode = generateAuthCodeFirst(first20);
        if ("Infinite".equalsIgnoreCase(type)) {
            return new UniRes(null, first20 + authCode, "Infinite", name, description, new ArrayList<>());
        }

        throw new IllegalArgumentException("不支持URC类型: " + type);
    }

    // -------------------------- 原材料关系 --------------------------

    public void addMaterial(UniRes parent, UniRes child) {
        if (parent.getMaterials() == null) parent.setMaterials(new ArrayList<>());
        parent.getMaterials().add(child);
    }

    // -------------------------- 操作记录 --------------------------

    private final List<UrcOperation> operations = new ArrayList<>();

    public UrcOperation recordOperation(String urc, String type, String factoryId, String operationId, String authCode) {
        UrcOperation op = new UrcOperation();
        op.setId((long) (operations.size() + 1));
        op.setUrc(urc);
        op.setType(type);
        op.setFactoryId(factoryId);
        op.setOperationId(operationId);
        op.setAuthCode(authCode);
        op.setTime();
        operations.add(op);
        return op;
    }

    public List<UrcOperation> getAllOperations() {
        return operations;
    }

    public List<UrcOperation> getOperationsByFactory(String factoryId) {
        return operations.stream().filter(o -> o.getFactoryId().equals(factoryId)).toList();
    }

    // -------------------------- 查询URC多语言 --------------------------

    /**
     * 获取语言优先名
     */
    public String getLangName(Map<String, String> nameMap, HttpServletRequest request) {
        String lang = "en"; // 默认
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("LANG".equalsIgnoreCase(cookie.getName()) && cookie.getValue() != null) {
                    lang = cookie.getValue().toLowerCase();
                    break;
                }
            }
        }
        return nameMap.getOrDefault(lang, nameMap.getOrDefault("en", ""));
    }
}