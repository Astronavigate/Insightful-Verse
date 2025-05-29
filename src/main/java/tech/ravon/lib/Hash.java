/*
package tech.eagloxis.lib;

import java.util.Scanner;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.apache.commons.codec.digest.DigestUtils;

public class Hash {
    public static void main(String[] args) {
        try (
        	Scanner in = new Scanner(System.in)) {
			String pwd = in.nextLine();
			System.out.println(calculate(pwd));
		}
    }

    public static String calculate(String pwd) {
    	pwd = DigestUtils.sha3_512Hex(DigestUtils.sha3_512Hex(pwd));
		System.out.println(pwd);
    	return pwd;
    }
}
Hash Pwd f586dda44df3da412755507f4b0bd4cc3926cf7aca65322f6f0d7197a9d2fdf81dbc04980654bbbbc2cfd223f8802c709f0b5fa0dbcdf627bbd894115c438990
*/

package tech.ravon.lib;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import java.util.Scanner;

// New Hash Pwd $argon2id$v=19$m=65536,t=3,p=1$55CmHLytIqlWdQguB+chGw$uHhLgR88RwGNhxu35KmFSQSfW2VwLotYWjHUtfdDEXI
public class Hash {
	public static void main(String[] args) {
		try (Scanner in = new Scanner(System.in)) {
			System.out.println("请输入密码：");
			String pwd = in.nextLine();
			String hashedPwd = calculate(pwd);
			System.out.println("哈希后的密码：" + hashedPwd);
		}
	}

	public static String calculate(String pwd) {
		// 创建 Argon2 实例，使用 Argon2id 算法
		Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
		String hash;
		try {
			// 配置参数：内存消耗、并发线程数、迭代次数
			int iterations = 3;    // 迭代次数
			int memory = 65536;    // 内存消耗（以 KB 为单位）
			int parallelism = 1;   // 并发线程数

			// 哈希生成
			hash = argon2.hash(iterations, memory, parallelism, pwd);
		} finally {
			argon2.wipeArray(pwd.toCharArray()); // 清理密码数组，确保内存安全
		}
		return hash;
	}

	// 验证密码
	public static boolean verify(String hashedPwd, String inputPwd) {
		Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
		return argon2.verify(hashedPwd, inputPwd);
	}
}
