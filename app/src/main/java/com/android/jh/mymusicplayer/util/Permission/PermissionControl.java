package com.android.jh.mymusicplayer.util.Permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * 권한 처리를 담당하는 클래스
 *
 * 권한 변경시 PERMISSION_ARRAY의 값만 변경 해주면 된다.
 */

public class PermissionControl {
    // 1.2 요청할 권한 목록 작성
    public static final String PERMISSION_ARRAY[] = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    //권한 체크
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkPermssion(Activity activity, int req_permission) {
        // 1.1 런타임 권한체크
        boolean permCheck = true;
        for (String perm : PERMISSION_ARRAY){
            if(activity.checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                permCheck = false;
                break;
            }
        }
        // 퍼미션이 모두 true 이면 프로그램 실행
        if(permCheck) {
            return true;
        } else{ // 아닐경우
            // 1.3 시스템에 권한요청
            activity.requestPermissions(PERMISSION_ARRAY, req_permission);
            return false;
        }
    }

    //권한체크 후 콜백 처리
    public static boolean onCheckedResult(int[] grantResults) {
        boolean checkResult = true;
        // 권한처리 결과값을 반복문을 돌면서 확인한 후 하나라도 승인 되지않았다면
        // false를 리턴
        for(int result : grantResults) {
            if(result != PackageManager.PERMISSION_GRANTED) {
                checkResult = false;
            }
        }
        return checkResult;
    }
}
