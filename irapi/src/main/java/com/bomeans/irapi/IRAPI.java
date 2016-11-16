package com.bomeans.irapi;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bomeans.IRKit.BIRRemote;
import com.bomeans.IRKit.BIRTVPicker;
import com.bomeans.IRKit.BrandItem;
import com.bomeans.IRKit.ConstValue;
import com.bomeans.IRKit.IRKit;
import com.bomeans.IRKit.IRemoteCreateCallBack;
import com.bomeans.IRKit.IWebAPICallBack;
import com.bomeans.IRKit.KeyName;
import com.bomeans.IRKit.ModelItem;
import com.bomeans.IRKit.TypeItem;
import com.bomeans.IRKit.VoiceSearchResultItem;
import com.bomeans.wifi2ir.ISmartLinkCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper for the com.bomeans.IRKit and com.bomeans.wifi2ir functionalities.
 * 
 * @author Bomeans Design.
 *
 */
public class IRAPI {
	private static final String TAG = "sss";

	public static void init(String apiKey, Context appContext) {
		IRKit.setup(apiKey, appContext);
	}

	public static void switchToChineseServer(Boolean set) {
		IRKit.setUseChineseServer(set);
	}

	public static void setCustomerIrBlaster(IIRBlaster irBlaster) {
		IRKit.setIRHW(new IrBlasterWrapper(irBlaster));
	}

	/**
	 * Get the remote type (category) list.
	 *
	 * @param locationCode location code (e.g. "CN")
	 * @param getNew true if fetch the list from server, or false to use cached data
	 * @param callback the type list is sent to this callback.
	 * @return true if successfully executed, false otherwise
	 */
	public static Boolean getTypeList(String locationCode, Boolean getNew, final IGetTypeListCallback callback) {

		AsyncTask<?,?,?> task = IRKit.webGetTypeList(locationCode, getNew, new IWebAPICallBack() {

			@Override
			public void onPreExecute() {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPostExecute(Object arrayObj, int error) {

				List<TypeInfo> typeList = new ArrayList<TypeInfo>();

				if (error == ConstValue.BIRNoError) {
	                TypeItem[] typeItemArray = (TypeItem[]) arrayObj;
	                for (int i = 0; i < typeItemArray.length; i++) {
	                	typeList.add(new TypeInfo(
	                			typeItemArray[i].typeId,
	                			typeItemArray[i].name,
	                			typeItemArray[i].locationName));
	                }

	                if (null != callback) {
	                	callback.onDataReceived(typeList);
	                }
	            } else {
	            	if (null != callback) {
	            		callback.onError(error);
	            	}
	            }

			}

			@Override
			public void onProgressUpdate(Integer... progress) {
				// TODO Auto-generated method stub	
			}

		});

		return null != task;
	}

	/**
	 * Get the brand list of specified type(category)
	 * @param locationCode location code (e.g. "CN")
	 * @param typeId type id
	 * @param sortByRanking the returned brand list is sorted by ranking of popularity
	 * @param getNew true if fetch the list from server, or false to use cached data
	 * @param callback the brand list is sent to this callback.
	 * @return true if successfully executed, false otherwise
	 */
	public static Boolean getBrandList(String locationCode, String typeId, Boolean sortByRanking, Boolean getNew, final IGetBrandListCallback callback) {

		AsyncTask<?, ?, ?> task;

		if (sortByRanking) {

			task = IRKit.webGetTopBrandList(typeId, 0, 10000, locationCode, getNew, new IWebAPICallBack() {
				@Override
				public void onPreExecute() {
					// TODO Auto-generated method stub	
				}

				@Override
				public void onPostExecute(Object arrayObj, int error) {
					List<BrandInfo> brandList = new ArrayList<BrandInfo> ();

					if (error == ConstValue.BIRNoError) {
		                BrandItem[] brandItemArray = (BrandItem[]) arrayObj;
		                for (int i = 0; i < brandItemArray.length; i++) {

		                	brandList.add(new BrandInfo(
		                			brandItemArray[i].brandId,
		                			brandItemArray[i].name,
		                			brandItemArray[i].locationName
		                			));
		                }

		                if (null != callback) {
		                	callback.onDataReceived(brandList);
		                }

		            } else {
		            	if (null != callback) {
		            		callback.onError(error);
		            	}
		            }
				}

				@Override
				public void onProgressUpdate(Integer... progress) {
					// TODO Auto-generated method stub
				}
			});

		} else {

			task = IRKit.webGetBrandList(typeId, 0, 10000, locationCode, null, getNew, new IWebAPICallBack(){
				@Override
				public void onPreExecute() {
					// TODO Auto-generated method stub	
				}

				@Override
				public void onPostExecute(Object arrayObj, int error) {
					List<BrandInfo> brandList = new ArrayList<BrandInfo> ();

					if (error == ConstValue.BIRNoError) {
		                BrandItem[] brandItemArray = (BrandItem[]) arrayObj;
		                for (int i = 0; i < brandItemArray.length; i++) {

		                	brandList.add(new BrandInfo(
		                			brandItemArray[i].brandId,
		                			brandItemArray[i].name,
		                			brandItemArray[i].locationName
		                			));
		                }

		                if (null != callback) {
		                	callback.onDataReceived(brandList);
		                }

		            } else {
		            	if (null != callback) {
		            		callback.onError(error);
		            	}
		            }
				}

				@Override
				public void onProgressUpdate(Integer... progress) {
					// TODO Auto-generated method stub
				}
			});

		}

		return null != task;
	}

	/**
	 *
	 * @param typeId
	 * @param brandId
	 * @param getNew
	 * @param callback
	 * @return true if successfully executed, false otherwise
	 */
	public static Boolean getRemoteList(String typeId, String brandId, Boolean getNew, final IGetRemoteListCallback callback) {

		AsyncTask<?, ?, ?> task = IRKit.webGetRemoteModelList(typeId, brandId, new IWebAPICallBack() {

			@Override
			public void onPreExecute() {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPostExecute(Object arrayObj, int error) {

				if (error == ConstValue.BIRNoError) {

					List<RemoteInfo> remoteList = new ArrayList<RemoteInfo>();

					ModelItem[] modelItemArray = (ModelItem[]) arrayObj;
					for (int i = 0; i < modelItemArray.length; i++) {
						remoteList.add(new RemoteInfo(
								modelItemArray[i].model,
								modelItemArray[i].machineModel,
								modelItemArray[i].country,
								modelItemArray[i].releaseTime));
					}

					if (null != callback) {
						callback.onDataReceived(remoteList);
					}
				} else {
					if (null != callback) {
						callback.onError(error);
					}
				}

			}

			@Override
			public void onProgressUpdate(Integer... progress) {
				// TODO Auto-generated method stub	
			}

		});

		return null != task;
	}

	/**
	 *
	 * @param typeId
	 * @param locationCode
	 * @param getNew
	 * @param callback
	 * @return true if successfully executed, false otherwise
	 */
	public static Boolean getAvailableKeyList(String typeId, String locationCode, Boolean getNew, final IGetAvailableKeyListCallback callback) {

		AsyncTask<?, ?, ?> task = IRKit.webGetKeyName(typeId, locationCode, getNew, new IWebAPICallBack() {

			@Override
			public void onPreExecute() {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPostExecute(Object arrayObj, int error) {
				if (null != callback) {
					if (error == ConstValue.BIRNoError) {
						List<KeyInfo2> keyList = new ArrayList<KeyInfo2>();
						KeyName[] keyNameArray = (KeyName[]) arrayObj;
						for (int i = 0; i < keyNameArray.length; i++) {
							keyList.add(new KeyInfo2(keyNameArray[i]));
						}

						callback.onDataReceived(keyList);

					} else {
						callback.onError(error);
					}
				}

			}

			@Override
			public void onProgressUpdate(Integer... progress) {
				// TODO Auto-generated method stub
			}

		});

		return null != task;
	}

	/**
	 *
	 * @param searchString
	 * @param locationCode
	 * @param callback
	 * @return
	 */
	public static Boolean searchRemote(String searchString, String locationCode, final ISearchRemoteCallback callback) {

		AsyncTask<?, ?, ?> task = IRKit.webVSearch(searchString, locationCode, new IWebAPICallBack() {

			@Override
			public void onPreExecute() {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPostExecute(Object arrayObj, int error) {
				if (null != callback) {
					if (error == ConstValue.BIRNoError) {

						VoiceSearchResultItem result = (VoiceSearchResultItem) arrayObj;
						SearchResult searchResult = new SearchResult();
						if (result.typeItem != null & result.brandItem != null & result.keyItem != null) {
							searchResult.typeInfo = new TypeInfo(result.typeItem);
							searchResult.brandInfo = new BrandInfo(result.brandItem);
							searchResult.keyInfo = new KeyInfo(result.keyItem);
						} else if (result.typeItem != null & result.brandItem != null & result.keyItem == null) {
							searchResult.typeInfo = new TypeInfo(result.typeItem);
							searchResult.brandInfo = new BrandInfo(result.brandItem);
							searchResult.keyInfo = null;
						} else if (result.typeItem == null & result.brandItem == null & result.keyItem != null) {
							searchResult.keyInfo = new KeyInfo(result.keyItem);
						}

						callback.onResultReceived(searchResult);

					} else {
						callback.onError(error);
					}
				}
			}

			@Override
			public void onProgressUpdate(Integer... progress) {
				// TODO Auto-generated method stub
			}

		});

		return null != task;
	}

	/**
	 *
	 * @param typeId
	 * @param brandId
	 * @param remoteId
	 * @param getNew
	 * @param callback
	 * @return true if successfully executed, false otherwise
	 */
	public static Boolean createRemote(String typeId, String brandId, String remoteId, Boolean getNew, final ICreateRemoteCallback callback) {

		AsyncTask<?, ?, ?> task = IRKit.createRemote(typeId, brandId, remoteId, getNew, new IRemoteCreateCallBack() {

			@Override
			public void onCreateResult(Object remote, int result) {
				if (null != callback) {
					if (result == ConstValue.BIRNoError) {
						BIRRemote birRemote = (BIRRemote) remote;
						IRRemote irRemote = new IRRemote(birRemote);
						callback.onRemoteCreated(irRemote);
					} else {
						callback.onError(result);
					}
				}
			}

			@Override
			public void onPreCreate() {
				// TODO Auto-generated method stub	
			}

			@Override
			public void onProgressUpdate(Integer... progress) {
				// TODO Auto-generated method stub	
			}

		});

		return null != task;

	}

	/**
	 * Create universal remote with only limited keys
	 * @param typeId
	 * @param brandId
	 * @param getNew
	 * @param callback
	 * @return true if successfully executed, false otherwise
	 */
	public static Boolean createSimplifiedUniversalRemote(String typeId, String brandId, Boolean getNew, final ICreateRemoteCallback callback) {

		AsyncTask<?, ?, ?> task = IRKit.createRemote(typeId, brandId, null, getNew, new IRemoteCreateCallBack() {

			@Override
			public void onCreateResult(Object remote, int result) {
				if (null != callback) {
					if (result == ConstValue.BIRNoError) {
						BIRRemote birRemote = (BIRRemote)remote;
						IRRemote irRemote = new IRRemote(birRemote);
						callback.onRemoteCreated(irRemote);
					} else {
						callback.onError(result);
					}
				}
			}

			@Override
			public void onPreCreate() {
				// TODO Auto-generated method stub	
			}

			@Override
			public void onProgressUpdate(Integer... progress) {
				// TODO Auto-generated method stub	
			}

		});

		return null != task;
	}

	/**
	 * Create universal remote with full keys, not support AC type.
	 * @param typeId
	 * @param brandId
	 * @param getNew
	 * @param callback
	 * @return true if successfully executed, false otherwise
	 */
	public static Boolean createFullUniversalRemote(String typeId, String brandId, Boolean getNew, final ICreateRemoteCallback callback) {

		AsyncTask<?, ?, ?> task = IRKit.createBigCombineRemote(typeId, brandId, getNew, new IRemoteCreateCallBack() {

			@Override
			public void onCreateResult(Object remote, int result) {
				if (null != callback) {
					Log.e(TAG, result + "big");
					if (result == ConstValue.BIRNoError) {
						BIRRemote birRemote = (BIRRemote) remote;
						IRRemote irRemote = new IRRemote(birRemote);
						callback.onRemoteCreated(irRemote);
					} else {
						callback.onError(result);
					}
				}
			}

			@Override
			public void onPreCreate() {
				// TODO Auto-generated method stub	
			}

			@Override
			public void onProgressUpdate(Integer... progress) {
				// TODO Auto-generated method stub	
			}

		});

		return null != task;
	}

	public static Boolean createSmartPicker(String typeId, String brandId, Boolean getNew, final ICreateSmartPickerCallback callback) {

		AsyncTask<?, ?, ?> task = IRKit.createSmartPicker(typeId, brandId, getNew, new IRemoteCreateCallBack() {

			@Override
			public void onCreateResult(Object remote, int result) {
				if (null != callback) {

					if (result == ConstValue.BIRNoError) {
						TVSmartPicker tvSmartPicker = new TVSmartPicker((BIRTVPicker) remote);
						callback.onPickerCreated(tvSmartPicker);
					} else {
						callback.onError(result);
					}
				}
			}

			@Override
			public void onPreCreate() {
				// TODO Auto-generated method stub
			}

			@Override
			public void onProgressUpdate(Integer... progress) {
				// TODO Auto-generated method stub
			}

		});

		return null != task;
	}


	//----------------------------------------------------------------------------------
	// WiFi to IR functions
	//----------------------------------------------------------------------------------
	public static void wifi2irSetIP(String IrwifiIP){
		IRKit.SetWifiToIRIp(IrwifiIP);
	}

	public static Boolean wifi2irSearchDevice(int timeoutMsec, final ISmartLinkCallback callback) {

		AsyncTask<?, ?, ?> task = IRKit.wifi2IrIsConnect(callback, timeoutMsec);

		return null != task;
	}

	public static void wifi2irStartSmartConnection(String ssid, String password, byte authMode) {
		IRKit.startWifiToIRConnectToAP(ssid, password, authMode);
	}

	public static void wifi2irStopSmartConnection() {
		IRKit.stopWifiToIRConnect();
	}

	public static void wifi2irEnableLed(Boolean enable) {
		IRKit.wifiIRLed_OnOff(enable);
	}

	public static void wifi2irSetLedColor(float r, float g, float b) {
		IRKit.wifiIRLed_Color(r, g, b);
	}

	public static void wifi2irSetLedOnTimer(Boolean enable, int hour, int min, int sec) {
		IRKit.wifiIRLed_SetOnTimer(enable, hour, min, sec);
	}

	public static void wifi2irSetLedOffTimer(Boolean enable, int hour, int min, int sec) {
		IRKit.wifiIRLed_SetOffTimer(enable, hour, min, sec);
	}

	public static void wifi2irSetWifiOnTimer(Boolean enable, int hour, int min, int sec) {
		IRKit.wifiIR_SetOnTimer(enable, hour, min, sec);
	}

	public static void wifi2irSetWifiOffTimer(Boolean enable, int hour, int min, int sec) {
		IRKit.wifiIR_SetOffTimer(enable, hour, min, sec);
	}

	public static void wifi2irSetWifiOffNow() {
		IRKit.wifiIR_TuneOffWifi();
	}

//----------------------------------------------------------------------------------

	public static void wifi2irSwitch(Boolean enable){
		IRKit.wifiIRSwitch_OnOff(enable);
	}

	public static void wifi2irOnTimer(Boolean enable, int hour, int min, int sec){
		IRKit.wifiIRSwitch_SetOnTimer(enable, hour, min, sec);
	}
	public static void wifi2irOffTimer(Boolean enable, int hour, int min, int sec){
		IRKit.wifiIRSwitch_SetOffTimer(enable,hour,min,sec);
	}
}











