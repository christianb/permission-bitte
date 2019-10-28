package com.sensorberg.permissionbitte;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PermissionsTest {

  @Test(expected = NullPointerException.class)
  public void creating_permissions_with_null_map_should_throw_nullpointerexception() {
    new Permissions(null);
  }

  @Test
  public void getPermissionSet_should_be_empty_when_no_permissions_available() {
    Permissions classUnderTest = new Permissions(Collections.<String, PermissionResult>emptyMap());

    Set<Permission> result = classUnderTest.getPermissionSet();

    Assert.assertTrue(result.isEmpty());
  }

  @Test
  public void getPermissionSet_should_reflect_permission() {
    Map<String, PermissionResult> map = new HashMap<>();
    map.put("permissionA", PermissionResult.REQUEST_PERMISSION);
    map.put("permissionB", PermissionResult.DENIED);
    map.put("permissionC", PermissionResult.GRANTED);
    map.put("permissionD", PermissionResult.SHOW_RATIONALE);

    Permissions classUnderTest = new Permissions(map);

    for (Permission permission : classUnderTest.getPermissionSet()) {
      switch (permission.getName()) {
        case "permissionA":
          Assert.assertEquals(PermissionResult.REQUEST_PERMISSION, permission.getResult());
          break;
        case "permissionB":
          Assert.assertEquals(PermissionResult.DENIED, permission.getResult());
          break;
        case "permissionC":
          Assert.assertEquals(PermissionResult.GRANTED, permission.getResult());
          break;
        case "permissionD":
          Assert.assertEquals(PermissionResult.SHOW_RATIONALE, permission.getResult());
          break;
      }
    }
  }

  @Test
  public void filter_should_return_empty_set_when_map_is_empty() {
    Permissions classUnderTest = new Permissions(Collections.<String, PermissionResult>emptyMap());

    Set<Permission> result = classUnderTest.filter(null);

    Assert.assertTrue(result.isEmpty());
  }

  @Test
  public void filter_should_return_empty_set_when_permissionResult_is_not_in_map() {
    Map<String, PermissionResult> map = new HashMap<>();
    map.put("permissionA", PermissionResult.REQUEST_PERMISSION);

    Permissions classUnderTest = new Permissions(map);

    Set<Permission> result = classUnderTest.filter(PermissionResult.DENIED);

    Assert.assertTrue(result.isEmpty());
  }

  @Test
  public void filter_should_return_set_for_permissionResult() {
    Map<String, PermissionResult> map = new HashMap<>();
    map.put("permissionA", PermissionResult.REQUEST_PERMISSION);
    map.put("permissionB", PermissionResult.DENIED);

    Permissions classUnderTest = new Permissions(map);

    Set<Permission> result = classUnderTest.filter(PermissionResult.REQUEST_PERMISSION);

    Assert.assertEquals(1, result.size());
  }

  @Test
  public void deniedPermanently_should_return_true_when_at_least_one_permissionResult_is_DENIED() {
    Map<String, PermissionResult> map = new HashMap<>();
    map.put("permissionA", PermissionResult.REQUEST_PERMISSION);
    map.put("permissionB", PermissionResult.DENIED);
    map.put("permissionC", PermissionResult.GRANTED);
    map.put("permissionD", PermissionResult.SHOW_RATIONALE);

    Permissions classUnderTest = new Permissions(map);

    boolean result = classUnderTest.deniedPermanently();

    Assert.assertTrue(result);
  }

  @Test
  public void deniedPermanently_should_return_false_when_no_permissionResult_is_DENIED() {
    Map<String, PermissionResult> map = new HashMap<>();
    map.put("permissionA", PermissionResult.REQUEST_PERMISSION);
    map.put("permissionC", PermissionResult.GRANTED);
    map.put("permissionD", PermissionResult.SHOW_RATIONALE);

    Permissions classUnderTest = new Permissions(map);

    boolean result = classUnderTest.deniedPermanently();

    Assert.assertFalse(result);
  }

  @Test
  public void deniedPermanently_should_return_false_when_map_is_empty() {
    Permissions classUnderTest = new Permissions(Collections.<String, PermissionResult>emptyMap());

    boolean result = classUnderTest.deniedPermanently();

    Assert.assertFalse(result);
  }

  @Test
  public void showRationale_should_return_true_when_at_least_one_permissionResult_is_SHOW_RATIONALE() {
    Map<String, PermissionResult> map = new HashMap<>();
    map.put("permissionA", PermissionResult.REQUEST_PERMISSION);
    map.put("permissionB", PermissionResult.DENIED);
    map.put("permissionC", PermissionResult.GRANTED);
    map.put("permissionD", PermissionResult.SHOW_RATIONALE);

    Permissions classUnderTest = new Permissions(map);

    boolean result = classUnderTest.showRationale();

    Assert.assertTrue(result);
  }

  @Test
  public void showRationale_should_return_false_when_no_permissionResult_is_SHOW_REATIONALE() {
    Map<String, PermissionResult> map = new HashMap<>();
    map.put("permissionA", PermissionResult.REQUEST_PERMISSION);
    map.put("permissionC", PermissionResult.GRANTED);
    map.put("permissionD", PermissionResult.DENIED);

    Permissions classUnderTest = new Permissions(map);

    boolean result = classUnderTest.showRationale();

    Assert.assertFalse(result);
  }

  @Test
  public void showRationale_should_return_false_when_map_is_empty() {
    Permissions classUnderTest = new Permissions(Collections.<String, PermissionResult>emptyMap());

    boolean result = classUnderTest.showRationale();

    Assert.assertFalse(result);
  }

  @Test
  public void needAskingForPermission_should_return_true_when_at_least_one_permissionResult_is_REQUEST_PERMISSION() {
    Map<String, PermissionResult> map = new HashMap<>();
    map.put("permissionA", PermissionResult.REQUEST_PERMISSION);
    map.put("permissionB", PermissionResult.DENIED);
    map.put("permissionC", PermissionResult.GRANTED);
    map.put("permissionD", PermissionResult.SHOW_RATIONALE);

    Permissions classUnderTest = new Permissions(map);

    boolean result = classUnderTest.needAskingForPermission();

    Assert.assertTrue(result);
  }

  @Test
  public void needAskingForPermission_should_return_false_when_no_permissionResult_is_REQUEST_PERMISSION() {
    Map<String, PermissionResult> map = new HashMap<>();
    map.put("permissionA", PermissionResult.SHOW_RATIONALE);
    map.put("permissionC", PermissionResult.GRANTED);
    map.put("permissionD", PermissionResult.DENIED);

    Permissions classUnderTest = new Permissions(map);

    boolean result = classUnderTest.needAskingForPermission();

    Assert.assertFalse(result);
  }

  @Test
  public void needAskingForPermission_should_return_false_when_map_is_empty() {
    Permissions classUnderTest = new Permissions(Collections.<String, PermissionResult>emptyMap());

    boolean result = classUnderTest.needAskingForPermission();

    Assert.assertFalse(result);
  }

  @Test
  public void allGranted() {
  }

  @Test
  public void allGranted_should_return_true_when_all_permissionResult_are_GRANTED() {
    Map<String, PermissionResult> map = new HashMap<>();
    map.put("permissionA", PermissionResult.GRANTED);
    map.put("permissionB", PermissionResult.GRANTED);
    map.put("permissionC", PermissionResult.GRANTED);
    map.put("permissionD", PermissionResult.GRANTED);

    Permissions classUnderTest = new Permissions(map);

    boolean result = classUnderTest.allGranted();

    Assert.assertTrue(result);
  }

  @Test
  public void allGranted_should_return_false_when_not_all_permissionResult_are_GRANTED() {
    Map<String, PermissionResult> map = new HashMap<>();
    map.put("permissionA", PermissionResult.SHOW_RATIONALE);
    map.put("permissionC", PermissionResult.GRANTED);
    map.put("permissionB", PermissionResult.DENIED);
    map.put("permissionD", PermissionResult.REQUEST_PERMISSION);

    Permissions classUnderTest = new Permissions(map);

    boolean result = classUnderTest.allGranted();

    Assert.assertFalse(result);
  }

  @Test
  public void allGranted_should_return_true_when_map_is_empty() {
    Permissions classUnderTest = new Permissions(Collections.<String, PermissionResult>emptyMap());

    boolean result = classUnderTest.allGranted();

    Assert.assertTrue(result);
  }
}