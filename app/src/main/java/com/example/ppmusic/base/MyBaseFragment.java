package com.example.ppmusic.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ppmusic.MyApp;
import com.example.ppmusic.interfaces.BaseInterface;
import com.example.ppmusic.utils.ToastUtils;


public abstract class MyBaseFragment extends Fragment implements BaseInterface {

	ProgressDialog progressDialog;
	ToastUtils toastUtils;

	public View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = setContentUI(inflater, container);
		} else {
			if (rootView.getParent() != null) {
				((ViewGroup) rootView.getParent()).removeView(rootView);
			}
		}
		return rootView;
	}

	public View findViewById(int id) {
		return rootView.findViewById(id);
	}

	/** TODO 加载视图 */
	public abstract View setContentUI(LayoutInflater inflater, ViewGroup container);

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		toastUtils = MyApp.getInstance().getToastUtils();
		init();
	}

	@Override
	public void showToast(String str) {
		toastUtils.showToast(str);
	}

	@Override
	public void showProgress(String msg) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(getActivity());
		}
		progressDialog.setMessage(msg);
		progressDialog.setCancelable(true);
		progressDialog.show();
	}

	@Override
	public void showProgress(String msg, boolean flag) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(getActivity());
		}
		progressDialog.setMessage(msg);
		progressDialog.setCancelable(flag);
		progressDialog.setCanceledOnTouchOutside(flag);
		progressDialog.show();
	}

	@Override
	public void hideProgress() {
		if (progressDialog != null && progressDialog.isShowing())
			progressDialog.hide();
	}

	@Override
	public void finishUI() {
		getActivity().finish();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	public void onResume() {
		super.onResume();
	}

	public void onPause() {
		super.onPause();
	}

	public abstract void init();

}
