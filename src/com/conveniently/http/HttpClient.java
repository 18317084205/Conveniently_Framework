package com.conveniently.http;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.conveniently.http.entity.FileEntity;
import com.conveniently.http.itf.IResponse;
import com.conveniently.http.util.Request;

import android.text.TextUtils;

public class HttpClient {

	public boolean deBug;
	private final ExecutorService mExecutors;
	private ConcurrentHashMap<String, ArrayList<Request>> mParentRequest;
	private ConcurrentHashMap<String, Request> mSelfRequest;

	public static final int STATE_UPLOAD = 1;
	public static final int STATE_DOWNLOAD = 2;

	public String filePath;
	public ArrayList<FileEntity> fileEntities;

	public boolean isDeBug() {
		return deBug;
	}

	public void setDeBug(boolean deBug) {
		this.deBug = deBug;
	}

	private static class HttpClientCreate {
		private static HttpClient httpClient = new HttpClient();
	}

	public static HttpClient getInstance() {
		return HttpClientCreate.httpClient;
	}

	private HttpClient() {
		// TODO Auto-generated constructor stub
		mParentRequest = new ConcurrentHashMap<String, ArrayList<Request>>();
		mSelfRequest = new ConcurrentHashMap<String, Request>();
		mExecutors = Executors.newFixedThreadPool(5);
	}

	public <T> void get(String url, IResponse<T> response) {
		// TODO Auto-generated method stub
		get(null, url, response);
	}

	public <T> void get(String parentTag, String url, IResponse<T> response) {
		// TODO Auto-generated method stub
		get(parentTag, null, url, response);
	}

	public <T> void get(String parentTag, String selfTag, String url,
			IResponse<T> response) {
		// TODO Auto-generated method stub
		Request request = new Request(parentTag, selfTag, url,
				Request.RequestMethod.GET, response);
		request.execute(mExecutors);
		chacheTask(parentTag, selfTag, request);
	}

	public <T> void post(String url, IResponse<T> response) {
		// TODO Auto-generated method stub
		post(null, url, response);
	}

	public <T> void post(String parentTag, String url, IResponse<T> response) {
		// TODO Auto-generated method stub
		post(parentTag, null, url, response);
	}

	public <T> void post(String parentTag, String selfTag, String url,
			IResponse<T> response) {
		// TODO Auto-generated method stub
		Request request = new Request(parentTag, selfTag, url,
				Request.RequestMethod.POST, response);
		request.execute(mExecutors);
		chacheTask(parentTag, selfTag, request);
	}

	private void chacheTask(String parentTag, String selfTag, Request request) {
		// TODO Auto-generated method stub
		if (!TextUtils.isEmpty(parentTag)) {
			if (!mParentRequest.containsKey(parentTag)) {
				ArrayList<Request> requests = new ArrayList<Request>();
				mParentRequest.put(parentTag, requests);
			}
			mParentRequest.get(parentTag).add(request);
		}

		if (!TextUtils.isEmpty(selfTag)) {
			if (!mSelfRequest.containsKey(selfTag)) {
				mSelfRequest.put(selfTag, request);
			}
		}

	}

	/**
	 * @param tag
	 * @param force
	 *            true cancel task with no callback, false cancel task with
	 *            callback as CancelException
	 */
	public void cancelRequest(String parentTag, String selfTag) {

		if (!TextUtils.isEmpty(parentTag)) {
			if (mParentRequest.containsKey(parentTag)) {
				ArrayList<Request> requests = mParentRequest.remove(parentTag);
				for (Request request : requests) {
					if (!request.isCompleted && !request.isCancelled) {
						request.cancel(true);
					}
				}
			}
			return;
		}

		if (!TextUtils.isEmpty(selfTag)) {
			if (mSelfRequest.containsKey(selfTag)) {
				mSelfRequest.get(selfTag).cancel(true);
				mSelfRequest.remove(selfTag);
			}
		}

	}

	public void cancelAll() {
		for (ConcurrentHashMap.Entry<String, ArrayList<Request>> entry : mParentRequest
				.entrySet()) {
			ArrayList<Request> requests = entry.getValue();
			for (Request request : requests) {
				if (!request.isCompleted && !request.isCancelled) {
					request.cancel(true);
				}
			}
		}
		mParentRequest.clear();
	}
}
