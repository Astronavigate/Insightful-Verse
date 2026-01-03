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

package tech.ravon.model.inver;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

@TableName(value ="users")
@Data
public class User implements Serializable {

	@TableId(type = IdType.AUTO)
	private Long userId;

	private String username;

	private String email;

	private String phone;

	private String password;

	private Long classId;

	private String avatarUrl;

	private Object authority;

	@TableField(exist = false)
	private static final long serialVersionUID = 1L;

	@Override
	public boolean equals(Object that) {
		if (this == that) {
			return true;
		}
		if (that == null) {
			return false;
		}
		if (getClass() != that.getClass()) {
			return false;
		}
		User other = (User) that;
		return (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
				&& (this.getUsername() == null ? other.getUsername() == null : this.getUsername().equals(other.getUsername()))
				&& (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
				&& (this.getPhone() == null ? other.getPhone() == null : this.getPhone().equals(other.getPhone()))
				&& (this.getPassword() == null ? other.getPassword() == null : this.getPassword().equals(other.getPassword()))
				&& (this.getClassId() == null ? other.getClassId() == null : this.getClassId().equals(other.getClassId()))
				&& (this.getAvatarUrl() == null ? other.getAvatarUrl() == null : this.getAvatarUrl().equals(other.getAvatarUrl()))
				&& (this.getAuthority() == null ? other.getAuthority() == null : this.getAuthority().equals(other.getAuthority()));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
		result = prime * result + ((getUsername() == null) ? 0 : getUsername().hashCode());
		result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
		result = prime * result + ((getPhone() == null) ? 0 : getPhone().hashCode());
		result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
		result = prime * result + ((getClassId() == null) ? 0 : getClassId().hashCode());
		result = prime * result + ((getAvatarUrl() == null) ? 0 : getAvatarUrl().hashCode());
		result = prime * result + ((getAuthority() == null) ? 0 : getAuthority().hashCode());
		return result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		sb.append("Hash = ").append(hashCode());
		sb.append(", userId=").append(userId);
		sb.append(", username=").append(username);
		sb.append(", email=").append(email);
		sb.append(", phone=").append(phone);
		sb.append(", password=").append(password);
		sb.append(", classId=").append(classId);
		sb.append(", eagloxisId=").append(avatarUrl);
		sb.append(", authority=").append(authority);
		sb.append(", serialVersionUID=").append(serialVersionUID);
		sb.append("]");
		return sb.toString();
	}
}