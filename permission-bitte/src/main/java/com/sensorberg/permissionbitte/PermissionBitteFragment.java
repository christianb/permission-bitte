package com.sensorberg.permissionbitte;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.Map;
import java.util.Set;

/**
 * DO NOT USE THIS FRAGMENT DIRECTLY!
 * It's only here because fragments have to be public
 */
public class PermissionBitteFragment extends Fragment implements PermissionRationale {

  private static final int BITTE_LET_ME_PERMISSION = 23;

  private final PermissionBitteViewModel viewModel = new PermissionBitteViewModel();

  public PermissionBitteFragment() {
    setRetainInstance(true);
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    viewModel.requestPermissionsLiveData.observe(this, new Observer<Map<String, PermissionResult>>() {
      @Override
      public void onChanged(Map<String, PermissionResult> permissionMap) {
        if (permissionMap.isEmpty()) {
          // no permissions to handle
          getFragmentManager().beginTransaction().remove(PermissionBitteFragment.this).commitAllowingStateLoss();
          return;
        }

        Set<String> permissionNames = permissionMap.keySet();
        if (!permissionNames.isEmpty()) {
          requestPermissions(permissionNames.toArray(new String[0]), BITTE_LET_ME_PERMISSION);
        }
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();

    PackageManager packageManager = getActivity().getPackageManager();
    String packageName = getActivity().getPackageName();
    viewModel.onResume(isResumed(), packageManager, packageName, this);

  }

  void ask() {
    PackageManager packageManager = getActivity().getPackageManager();
    String packageName = getActivity().getPackageName();
    viewModel.ask(isResumed(), packageManager, packageName, this);
  }

  LiveData<Permissions> permissionLiveData() {
    return viewModel.permissionLiveData;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    if (requestCode != BITTE_LET_ME_PERMISSION || permissions.length <= 0) {
      return;
    }

    viewModel.onRequestPermissionsResult(permissions, grantResults, this);
  }
}
