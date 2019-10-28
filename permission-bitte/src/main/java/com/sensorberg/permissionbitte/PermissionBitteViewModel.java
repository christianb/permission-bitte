package com.sensorberg.permissionbitte;

import android.annotation.TargetApi;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class PermissionBitteViewModel extends ViewModel {

  private boolean askForPermission = false;

  private MutableLiveData<Map<String, PermissionResult>> _requestPermissionsLiveData = new MutableLiveData<>();
  final LiveData<Map<String, PermissionResult>> requestPermissionsLiveData = _requestPermissionsLiveData;

  private final MutableLiveData<Permissions> mutableLiveData = new MutableLiveData<>();
  final LiveData<Permissions> permissionLiveData = mutableLiveData;

  void onResume(boolean resumed,
                PackageManager packageManager,
                String packageName,
                PermissionRationale permissionRationale) {
    if (askForPermission) {
      askForPermission = false;
      ask(resumed, packageManager, packageName, permissionRationale);
    } else {
      updatePermissions(packageManager, packageName, permissionRationale);
    }
  }

  void ask(boolean resumed,
           PackageManager packageManager,
           String packageName,
           PermissionRationale permissionRationale) {
    if (!resumed) {
      askForPermission = true;
      return;
    }

    Map<String, PermissionResult> allPermissions = getPermissions(packageManager, packageName, permissionRationale);
    _requestPermissionsLiveData.setValue(allPermissions);
  }

  void onRequestPermissionsResult(@NonNull String[] permissionArray,
                                  @NonNull int[] grantResults,
                                  PermissionRationale permissionRationale) {
    Map<String, PermissionResult> permissionMap = new HashMap<>();

    for (int i = 0; i < permissionArray.length; i++) {
      final String name = permissionArray[i];

      if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
        if (permissionRationale.shouldShowRequestPermissionRationale(name)) {
          permissionMap.put(name, PermissionResult.SHOW_RATIONALE);
        } else {
          permissionMap.put(name, PermissionResult.DENIED);
        }

      } else {
        permissionMap.put(name, PermissionResult.GRANTED);
      }
    }

    setPermissions(new Permissions(permissionMap), true);
  }


  private void updatePermissions(PackageManager packageManager,
                                 String packageName,
                                 PermissionRationale permissionRationale) {
    Map<String, PermissionResult> permissionMap = getPermissions(packageManager, packageName, permissionRationale);

    // to not loose denied state during onResume(), permissionMap gets updated with previously DENIED permissions
    Permissions lastKnownPermissions = mutableLiveData.getValue();

    if (lastKnownPermissions != null) {
      Set<Permission> deniedPermissions = lastKnownPermissions.filter(PermissionResult.DENIED);

      for (Permission deniedPermission : deniedPermissions) {
        String deniedPermissionName = deniedPermission.getName();
        PermissionResult permissionResult = permissionMap.get(deniedPermissionName);

        if (permissionResult != null && permissionResult != PermissionResult.GRANTED) {
          permissionMap.put(deniedPermissionName, PermissionResult.DENIED);
        }
      }
    }

    setPermissions(new Permissions(permissionMap), false);
  }


  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  @NonNull
  private Map<String, PermissionResult> getPermissions(PackageManager packageManager,
                                                       String packageName,
                                                       PermissionRationale permissionRationale) {
    PackageInfo packageInfo = null;

    try {
      packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
    } catch (PackageManager.NameNotFoundException e) { /* ignore */ }

    Map<String, PermissionResult> permissions = new HashMap<>();
    if (packageInfo == null
            || packageInfo.requestedPermissions == null
            || packageInfo.requestedPermissionsFlags == null) {
      return permissions;
    }

    for (int i = 0; i < packageInfo.requestedPermissions.length; i++) {
      int flags = packageInfo.requestedPermissionsFlags[i];
      String group = null;

      try {
        group = packageManager.getPermissionInfo(packageInfo.requestedPermissions[i], 0).group;
      } catch (PackageManager.NameNotFoundException e) { /* ignore */ }

      String name = packageInfo.requestedPermissions[i];
      if (group != null) {
        if ((flags & PackageInfo.REQUESTED_PERMISSION_GRANTED) == 0) {
          if (permissionRationale.shouldShowRequestPermissionRationale(name)) {
            permissions.put(name, PermissionResult.SHOW_RATIONALE);
          } else {
            permissions.put(name, PermissionResult.REQUEST_PERMISSION);
          }
        } else {
          permissions.put(name, PermissionResult.GRANTED);
        }
      }
    }

    return permissions;
  }

  private void setPermissions(Permissions permissions, boolean forceUpdate) {
    if (forceUpdate) {
      mutableLiveData.setValue(permissions);
    } else {
      if (!permissions.equals(mutableLiveData.getValue())) {
        mutableLiveData.setValue(permissions);
      }
    }
  }
}
