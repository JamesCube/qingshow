package com.focosee.qingshow.httpapi.response.error;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.focosee.qingshow.util.ToastUtil;

/**
 * Created by zenan on 1/2/15.
 */
public class ErrorHandler {

    private static final String TAG = ErrorHandler.class.getSimpleName();

    public static void handle(Context context, int errorCode) {
        switch (errorCode) {
            case ErrorCode.ServerError:
                ToastUtil.showShortToast(context.getApplicationContext(), "请重试");
                break;
            case ErrorCode.IncorrectMailOrPassword:
                ToastUtil.showShortToast(context.getApplicationContext(), "账号或密码错误");
                break;
            case ErrorCode.SessionExpired:
                Log.d(TAG, "SessionExpired");
                break;
            case ErrorCode.ShowNotExist:
                Log.d(TAG, "ShowNotExist");
                break;
            case ErrorCode.ItemNotExist:
                Log.d(TAG, "ItemNotExist");
                break;
            case ErrorCode.PeopleNotExist:
                Log.d(TAG, "PeopleNotExist");
                break;
            case ErrorCode.InvalidEmail:
                ToastUtil.showShortToast(context.getApplicationContext(), "不合法的邮箱");
                break;
            case ErrorCode.NotEnoughParam:
                Log.d(TAG, "参数不够");
                break;
            case ErrorCode.PagingNotExist:
                Log.d(TAG, "没有更多数据了");
                break;
            case ErrorCode.EmailAlreadyExist:
                ToastUtil.showShortToast(context.getApplicationContext(), "账号已存在");
                break;
            case ErrorCode.AlreadyLikeShow:
                Log.d(TAG, "AlreadyLikeShow");
                break;
            case ErrorCode.NeedLogin:
                break;
            case ErrorCode.AlreadyFollowPeople:
                Log.d(TAG, "AlreadyFollowPeople");
                break;
            case ErrorCode.DidNotFollowPeople:
                Log.d(TAG, "DidNotFollowPeople");
                break;
            case ErrorCode.PItemNotExist:
                break;
            case ErrorCode.RequestValidationFail:
                Log.d(TAG, "RequestValidationFail");
                break;
            case ErrorCode.AlreadyRelated:
                Log.d(TAG, "AlreadyRelated");
                break;
            case ErrorCode.AlreadyUnrelated:
                Log.d(TAG, "AlreadyUnrelated");
                break;
            case ErrorCode.InvalidCurrentPassword:
                Log.d(TAG, "InvalidCurrentPassword");
                break;
            case ErrorCode.NoNetWork:
                ToastUtil.showShortToast(context.getApplicationContext(), "请检查网络");
                break;
        }
    }
}
