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

package tech.ravon.model.iviep;

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
	private Integer userId;

	private String username;

	private String email;

	private String phone;

	private String password;

	private Integer classId;

	private Integer eagloxisId;

	private String way1Id;

	private String way2Id;

	private String way3Id;

	private String way4Id;

	private String way5Id;

	private String way6Id;

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
				&& (this.getEagloxisId() == null ? other.getEagloxisId() == null : this.getEagloxisId().equals(other.getEagloxisId()))
				&& (this.getWay1Id() == null ? other.getWay1Id() == null : this.getWay1Id().equals(other.getWay1Id()))
				&& (this.getWay2Id() == null ? other.getWay2Id() == null : this.getWay2Id().equals(other.getWay2Id()))
				&& (this.getWay3Id() == null ? other.getWay3Id() == null : this.getWay3Id().equals(other.getWay3Id()))
				&& (this.getWay4Id() == null ? other.getWay4Id() == null : this.getWay4Id().equals(other.getWay4Id()))
				&& (this.getWay5Id() == null ? other.getWay5Id() == null : this.getWay5Id().equals(other.getWay5Id()))
				&& (this.getWay6Id() == null ? other.getWay6Id() == null : this.getWay6Id().equals(other.getWay6Id()))
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
		result = prime * result + ((getEagloxisId() == null) ? 0 : getEagloxisId().hashCode());
		result = prime * result + ((getWay1Id() == null) ? 0 : getWay1Id().hashCode());
		result = prime * result + ((getWay2Id() == null) ? 0 : getWay2Id().hashCode());
		result = prime * result + ((getWay3Id() == null) ? 0 : getWay3Id().hashCode());
		result = prime * result + ((getWay4Id() == null) ? 0 : getWay4Id().hashCode());
		result = prime * result + ((getWay5Id() == null) ? 0 : getWay5Id().hashCode());
		result = prime * result + ((getWay6Id() == null) ? 0 : getWay6Id().hashCode());
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
		sb.append(", eagloxisId=").append(eagloxisId);
		sb.append(", way1Id=").append(way1Id);
		sb.append(", way2Id=").append(way2Id);
		sb.append(", way3Id=").append(way3Id);
		sb.append(", way4Id=").append(way4Id);
		sb.append(", way5Id=").append(way5Id);
		sb.append(", way6Id=").append(way6Id);
		sb.append(", authority=").append(authority);
		sb.append(", serialVersionUID=").append(serialVersionUID);
		sb.append("]");
		return sb.toString();
	}
}