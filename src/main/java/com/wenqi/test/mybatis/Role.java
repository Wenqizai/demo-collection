package com.wenqi.test.mybatis;

/**
 * @author liangwenqi
 * @date 2023/4/14
 */
public class Role {

  private long id;
  private String roleName;
  private String note;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }
}
