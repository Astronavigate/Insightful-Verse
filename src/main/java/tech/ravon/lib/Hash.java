/**
 * Copyright 2025 Astronavigate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
