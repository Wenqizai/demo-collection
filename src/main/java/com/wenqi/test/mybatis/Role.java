package com.wenqi.test.mybatis;

/**
 * @author liangwenqi
 * @date 2023/4/14
 */
public class Role {

  public Role(long id, String roleName, String note) {
    this.id = id;
    this.roleName = roleName;
    this.note = note;
  }

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
