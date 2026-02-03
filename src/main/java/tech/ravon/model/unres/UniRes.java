package tech.ravon.model.unres;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UniRes {
    private Long id;
    private String urc;
    private String type;
    private String name;
    private String desc;
    private String company;
    private String companyDesc;
    private String factory;
    private String factoryDesc;
    private List<UniRes> materials;

    public UniRes() {
        this.id = null;
        this.urc = null;
        this.type = null;
        this.name = null;
        this.desc = null;
        this.materials = new ArrayList<>();
    }

    public UniRes(Long id, String urc, String type, String name, String description, List<UniRes> materials) {
        this.id = id;
        this.urc = urc;
        this.type = type;
        this.name = name;
        this.desc = description;
        this.materials = materials != null ? materials : new ArrayList<>();
    }

    public String getUrcBasic() {
        return urc.length() >= 10 ? urc.substring(0,10) : urc;
    }
    public String getUrcPlus() {
        return urc.length() >= 15 ? urc.substring(0,15) : urc;
    }
    public String getUrcInfinite() {
        return urc.length() >= 24 ? urc.substring(0,24) : urc;
    }
    public char getCheckBasic() {
        return urc.length() >= 10 ? urc.charAt(9) : '\0';
    }
    public char getCheckPlus() {
        return urc.length() >= 15 ? urc.charAt(14) : '\0';
    }
    public String getAuthCode() {
        return urc.length() >= 24 ? urc.substring(20,24) : "";
    }
}