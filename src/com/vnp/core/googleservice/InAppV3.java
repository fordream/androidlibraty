package com.vnp.core.googleservice;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.android.vending.billing.IInAppBillingService;
import com.vnp.core.common.LogUtils;

/**
 * <uses-permission android:name="com.android.vending.BILLING" />
 * 
 * @author truongvv
 * 
 */
public class InAppV3 {
	public interface InAppV3InitListener {
		public void start();

		/**
		 * 
		 * @param resultInit
		 */
		public void initResult(boolean resultInit);
	}

	private static InAppV3 commonInAppV3;
	private Context context;
	private List<PurcharseCallBack> lPurcharseCallBacks = new ArrayList<PurcharseCallBack>();
	private IabHelper mHelper;
	protected static final int RC_REQUEST = 10001;

	private InAppV3() {
	}

	public static InAppV3 getInstance() {
		if (commonInAppV3 == null) {
			commonInAppV3 = new InAppV3();
		}
		return commonInAppV3;
	}

	public void init(Context context, String base64EncodedPublicKey,
			final InAppV3InitListener listener) {
		// if (this.context == null) {
		listener.start();
		this.context = context;
		try {
			mHelper = new IabHelper(this.context, base64EncodedPublicKey);
			mHelper.enableDebugLogging(true);
			mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
				public void onIabSetupFinished(IabResult result) {

					if (!result.isSuccess()) {
						listener.initResult(false);
						return;
					}

					try {
						mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
							public void onQueryInventoryFinished(
									IabResult result, Inventory inventory) {

								if (result.isFailure()) {
									listener.initResult(false);
									return;
								} else {
									listener.initResult(true);
								}
							}
						});
					} catch (Exception exception) {
						listener.initResult(false);
					} catch (Error error) {
						listener.initResult(false);
					}
				}
			});

			context.bindService(new Intent(
					"com.android.vending.billing.InAppBillingService.BIND"),
					mServiceConn, Context.BIND_AUTO_CREATE);
		} catch (Exception exception) {
			listener.initResult(false);
		} catch (Error error) {
			listener.initResult(false);
		}
		// }
	}

	public void init(Context context, String base64EncodedPublicKey) {
		if (this.context == null) {
			this.context = context;
			try {
				mHelper = new IabHelper(this.context, base64EncodedPublicKey);
				mHelper.enableDebugLogging(true);
				mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
					public void onIabSetupFinished(IabResult result) {

						if (!result.isSuccess()) {
							return;
						}

						try {
							mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
								public void onQueryInventoryFinished(
										IabResult result, Inventory inventory) {

									if (result.isFailure()) {
										return;
									}
								}
							});
						} catch (Exception exception) {
						} catch (Error error) {
						}
					}
				});

				context.bindService(
						new Intent(
								"com.android.vending.billing.InAppBillingService.BIND"),
						mServiceConn, Context.BIND_AUTO_CREATE);
			} catch (Exception exception) {
			} catch (Error error) {
			}
		}
	}

	private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			String sku = null;
			if (purchase != null) {
				sku = purchase.getSku();
			}

			for (PurcharseCallBack purcharseCallBack : lPurcharseCallBacks)
				purcharseCallBack.onIabPurchaseFinish(result.isFailure(), sku);

			if (result.isFailure()) {
				return;
			}
		}
	};

	private IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			if (result.isSuccess()) {
			} else {
			}
		}
	};

	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (requestCode == RC_REQUEST)
				return !mHelper.handleActivityResult(requestCode, resultCode,
						data);
		} catch (Exception exception) {
		} catch (Error error) {
		}

		return false;
	}

	public void launchPurchaseFlow(Activity activity, String sku) {
		try {
			mHelper.launchPurchaseFlow(activity, sku, RC_REQUEST,
					mPurchaseFinishedListener);
		} catch (Exception exception) {
		} catch (Error error) {
		}
	}

	public boolean isPurchased(String sku1) {
		return queryingForPurchasedItems(sku1);
	}

	public boolean isPurchasedSub(String sku1) {
		return queryingForPurchasedItemsSub(sku1);
	}

	public boolean isPurchasedSubOrInapp(String sku1) {
		return isPurchased(sku1) || isPurchasedSub(sku1);
	}

	// ---------------------------------------------------------------------
	// Service
	// ---------------------------------------------------------------------
	private IInAppBillingService mService;

	private ServiceConnection mServiceConn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = IInAppBillingService.Stub.asInterface(service);
		}
	};

	public boolean queryingForPurchasedItems(String sku1) {
		Bundle ownedItems;
		try {
			ownedItems = mService.getPurchases(3, context.getPackageName(),
					"inapp", null);

			int response = ownedItems.getInt("RESPONSE_CODE");
			if (response == 0) {
				ArrayList ownedSkus = ownedItems
						.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
				ArrayList purchaseDataList = ownedItems
						.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
				ArrayList signatureList = ownedItems
						.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
				String continuationToken = ownedItems
						.getString("INAPP_CONTINUATION_TOKEN");

				for (int i = 0; i < purchaseDataList.size(); ++i) {
					// String purchaseData = purchaseDataList.get(i);
					// String signature = signatureList.get(i);
					String sku = ownedSkus.get(i).toString();
					if (sku.equals(sku1)) {
						return true;
					}
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	public List<InAppV3InforPurchaseItem> getInformationOfPurcharses(
			String[] skus) {
		List<InAppV3InforPurchaseItem> lInAppV3InforPurchaseItems = new ArrayList<InAppV3InforPurchaseItem>();
		if (skus != null) {
			for (int i = 0; i < skus.length; i++) {
				String sku = skus[i];

				InAppV3InforPurchaseItem InAppV3InforPurchaseItem = queryingForItemsAvailableForPurchaseForSub(sku);

				if (InAppV3InforPurchaseItem == null)
					InAppV3InforPurchaseItem = queryingForItemsAvailableForPurchase(sku);

				if (InAppV3InforPurchaseItem != null)
					lInAppV3InforPurchaseItems.add(InAppV3InforPurchaseItem);

			}
		}
		return lInAppV3InforPurchaseItems;
	}

	public InAppV3InforPurchaseItem queryingForItemsAvailableForPurchaseForSub(
			String sku1) {
		ArrayList skuList = new ArrayList();
		skuList.add(sku1);
		Bundle querySkus = new Bundle();
		querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
		try {

			Bundle skuDetails = mService.getSkuDetails(3,
					context.getPackageName(), "subs", querySkus);

			int response = skuDetails.getInt("RESPONSE_CODE");

			if (response == 0) {
				ArrayList responseList = skuDetails
						.getStringArrayList("DETAILS_LIST");
				for (Object thisResponse : responseList) {
					JSONObject object = new JSONObject(thisResponse.toString());
					String sku = object.getString("productId");
					String price = object.getString("price");

					if (sku.equals(sku1)) {
						InAppV3InforPurchaseItem InAppV3InforPurchaseItem = new InAppV3InforPurchaseItem();
						InAppV3InforPurchaseItem.setId(sku);
						InAppV3InforPurchaseItem.setSubs(true);
						InAppV3InforPurchaseItem.setPrice(price);
						return InAppV3InforPurchaseItem;
					}
				}
			}
		} catch (Exception e) {
		}

		return null;
	}

	public InAppV3InforPurchaseItem queryingForItemsAvailableForPurchase(
			String sku1) {
		ArrayList skuList = new ArrayList();
		skuList.add(sku1);
		Bundle querySkus = new Bundle();
		querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
		try {

			Bundle skuDetails = mService.getSkuDetails(3,
					context.getPackageName(), "inapp", querySkus);

			int response = skuDetails.getInt("RESPONSE_CODE");

			if (response == 0) {
				ArrayList responseList = skuDetails
						.getStringArrayList("DETAILS_LIST");
				for (Object thisResponse : responseList) {
					JSONObject object = new JSONObject(thisResponse.toString());
					String sku = object.getString("productId");
					String price = object.getString("price");

					if (sku.equals(sku1)) {
						InAppV3InforPurchaseItem InAppV3InforPurchaseItem = new InAppV3InforPurchaseItem();
						InAppV3InforPurchaseItem.setId(sku);
						InAppV3InforPurchaseItem.setSubs(false);
						InAppV3InforPurchaseItem.setPrice(price);
						return InAppV3InforPurchaseItem;
					}
				}
			}
		} catch (Exception e) {
		}

		return null;
	}

	private boolean queryingForPurchasedItemsSub(String sku1) {
		Bundle ownedItems;
		try {
			ownedItems = mService.getPurchases(3, context.getPackageName(),
					"subs", null);

			int response = ownedItems.getInt("RESPONSE_CODE");
			if (response == 0) {
				ArrayList ownedSkus = ownedItems
						.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");

				ArrayList purchaseDataList = ownedItems
						.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
				ArrayList signatureList = ownedItems
						.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");

				String continuationToken = ownedItems
						.getString("INAPP_CONTINUATION_TOKEN");

				LogUtils.e("getPurchases",
						"---------------------------------------");
				LogUtils.e("getPurchases", "response : " + response);
				LogUtils.e("getPurchases", "continuationToken : "
						+ continuationToken);

				if (purchaseDataList != null)
					LogUtils.e("getPurchases", "purchaseDataList : "
							+ purchaseDataList.toString());

				if (ownedSkus != null)
					LogUtils.e("getPurchases",
							"ownedSkus : " + ownedSkus.toString());

				if (signatureList != null) {
					LogUtils.e("getPurchases", "signatureList : "
							+ signatureList.toString());
				}
				LogUtils.e("getPurchases",
						"---------------------------------------");
				for (int i = 0; i < purchaseDataList.size(); ++i) {
					// String purchaseData = purchaseDataList.get(i);
					// String signature = signatureList.get(i);
					String sku = ownedSkus.get(i).toString();
					if (sku.equals(sku1)) {
						return true;
					}
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	public boolean isBillingSupportedInAppV3() {
		try {
			return mService.isBillingSupported((3), context.getPackageName(),
					"inapp") == 0;
		} catch (Exception e) {
			return false;
		}
	}

	public void registerPurcharseCallBacks(PurcharseCallBack purcharseCallBack) {
		if (purcharseCallBack != null
				&& !lPurcharseCallBacks.contains(purcharseCallBack)) {
			lPurcharseCallBacks.add(purcharseCallBack);
		}
	}

	public void unRegisterPurcharseCallBacks(PurcharseCallBack purcharseCallBack) {
		if (purcharseCallBack != null
				&& lPurcharseCallBacks.contains(purcharseCallBack)) {
			lPurcharseCallBacks.remove(purcharseCallBack);
		}
	}

	public interface PurcharseCallBack {
		public void onIabPurchaseFinish(boolean failure, String sku);
	}
}

// ===============================================================================
// IabHelper
// ===============================================================================
/**
 * Provides convenience methods for in-app billing. Create one instance of this
 * class for your application and use it to process in-app billing operations.
 * It provides synchronous (blocking) and asynchronous (non-blocking) methods
 * for many common in-app billing operations, as well as automatic signature
 * verification.
 * 
 * After instantiating, you must perform setup in order to start using the
 * object. To perform setup, call the {@link #startSetup} method and provide a
 * listener; that listener will be notified when setup is complete, after which
 * (and not before) you may call other methods.
 * 
 * After setup is complete, you may query whether the user owns a given item or
 * not by calling {@link #isOwned}, get all items owned with
 * {@link #getOwnedSkus}, get an item's price with {@link #getPrice}, amongst
 * others (see documentation for specific methods).
 * 
 * Please notice that the object will only have knowledge about owned items; it
 * will not automatically have information (such as price, description) for
 * items that are not owned by the user, because the server will not
 * automatically provide those. In order to query information for an item that's
 * not owned (such as to display the price to the user before a purchase), you
 * should first bring the item's sku to the object's knowledge by calling
 * {@link #addSku} and then perform an inventory refresh by calling
 * {@link #refreshInventory()} or its corresponding asynchronous version
 * {@link #refreshInventoryAsync}.
 * 
 * If you know the skus of all the items that you can possibly be interested in,
 * you can call {@link #addSku} for those items before {@link #startSetup}, and
 * that way all the information about them will be available from the start,
 * with no need to refresh the inventory later.
 * 
 * When you are done with this object, don't forget to call {@link #dispose} to
 * ensure proper cleanup. This object holds a binding to the in-app billing
 * service, which will leak unless you dispose of it correctly. If you created
 * the object on an Activity's onCreate method, then the recommended place to
 * dispose of it is the Activity's onDestroy method.
 * 
 * A note about threading: When using this object from a background thread, you
 * may call the blocking versions of methods; when using from a UI thread, call
 * only the asynchronous versions and handle the results via callbacks. Also,
 * notice that you can only call one asynchronous operation at a time;
 * attempting to start a second asynchronous operation while the first one has
 * not yet completed will result in an exception being thrown.
 * 
 * @author Bruno Oliveira (Google)
 * 
 */
class IabHelper {
	// Is debug logging enabled?
	boolean mDebugLog = false;
	String mDebugTag = "IabHelper";

	// Is setup done?
	boolean mSetupDone = false;

	// Is an asynchronous operation in progress?
	// (only one at a time can be in progress)
	boolean mAsyncInProgress = false;

	// (for logging/debugging)
	// if mAsyncInProgress == true, what asynchronous operation is in progress?
	String mAsyncOperation = "";

	// Context we were passed during initialization
	Context mContext;

	// Connection to the service
	IInAppBillingService mService;
	ServiceConnection mServiceConn;

	// The request code used to launch purchase flow
	int mRequestCode;

	// Public key for verifying signature, in base64 encoding
	String mSignatureBase64 = null;

	// Billing response codes
	public static final int BILLING_RESPONSE_RESULT_OK = 0;
	public static final int BILLING_RESPONSE_RESULT_USER_CANCELED = 1;
	public static final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;
	public static final int BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4;
	public static final int BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5;
	public static final int BILLING_RESPONSE_RESULT_ERROR = 6;
	public static final int BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7;
	public static final int BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8;

	// IAB Helper error codes
	public static final int IABHELPER_ERROR_BASE = -1000;
	public static final int IABHELPER_REMOTE_EXCEPTION = -1001;
	public static final int IABHELPER_BAD_RESPONSE = -1002;
	public static final int IABHELPER_VERIFICATION_FAILED = -1003;
	public static final int IABHELPER_SEND_INTENT_FAILED = -1004;
	public static final int IABHELPER_USER_CANCELLED = -1005;
	public static final int IABHELPER_UNKNOWN_PURCHASE_RESPONSE = -1006;
	public static final int IABHELPER_MISSING_TOKEN = -1007;
	public static final int IABHELPER_UNKNOWN_ERROR = -1008;

	// Keys for the responses from InAppBillingService
	public static final String RESPONSE_CODE = "RESPONSE_CODE";
	public static final String RESPONSE_GET_SKU_DETAILS_LIST = "DETAILS_LIST";
	public static final String RESPONSE_BUY_INTENT = "BUY_INTENT";
	public static final String RESPONSE_INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
	public static final String RESPONSE_INAPP_SIGNATURE = "INAPP_DATA_SIGNATURE";
	public static final String RESPONSE_INAPP_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
	public static final String RESPONSE_INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
	public static final String RESPONSE_INAPP_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";
	public static final String INAPP_CONTINUATION_TOKEN = "INAPP_CONTINUATION_TOKEN";

	// Item type: in-app item
	public static final String ITEM_TYPE_INAPP = "inapp";

	// some fields on the getSkuDetails response bundle
	public static final String GET_SKU_DETAILS_ITEM_LIST = "ITEM_ID_LIST";
	public static final String GET_SKU_DETAILS_ITEM_TYPE_LIST = "ITEM_TYPE_LIST";

	/**
	 * Creates an instance. After creation, it will not yet be ready to use. You
	 * must perform setup by calling {@link #startSetup} and wait for setup to
	 * complete. This constructor does not block and is safe to call from a UI
	 * thread.
	 * 
	 * @param ctx
	 *            Your application or Activity context. Needed to bind to the
	 *            in-app billing service.
	 * @param base64PublicKey
	 *            Your application's public key, encoded in base64. This is used
	 *            for verification of purchase signatures. You can find your
	 *            app's base64-encoded public key in your application's page on
	 *            Google Play Developer Console. Note that this is NOT your
	 *            "developer public key".
	 */
	public IabHelper(Context ctx, String base64PublicKey) {
		mContext = ctx.getApplicationContext();
		mSignatureBase64 = base64PublicKey;
		logDebug("IAB helper created.");
	}

	/**
	 * Enables or disable debug logging through LogCat.
	 */
	public void enableDebugLogging(boolean enable, String tag) {
		mDebugLog = enable;
		mDebugTag = tag;
	}

	public void enableDebugLogging(boolean enable) {
		mDebugLog = enable;
	}

	/**
	 * Callback for setup process. This listener's {@link #onIabSetupFinished}
	 * method is called when the setup process is complete.
	 */
	public interface OnIabSetupFinishedListener {
		/**
		 * Called to notify that setup is complete.
		 * 
		 * @param result
		 *            The result of the setup process.
		 */
		public void onIabSetupFinished(IabResult result);
	}

	/**
	 * Starts the setup process. This will start up the setup process
	 * asynchronously. You will be notified through the listener when the setup
	 * process is complete. This method is safe to call from a UI thread.
	 * 
	 * @param listener
	 *            The listener to notify when the setup process is complete.
	 */
	public void startSetup(final OnIabSetupFinishedListener listener) {
		// If already set up, can't do it again.
		if (mSetupDone)
			throw new IllegalStateException("IAB helper is already set up.");

		// Connection to IAB service
		logDebug("Starting in-app billing setup.");
		mServiceConn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				logDebug("Billing service disconnected.");
				mService = null;
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				logDebug("Billing service connected.");
				mService = IInAppBillingService.Stub.asInterface(service);
				String packageName = mContext.getPackageName();
				try {
					logDebug("Checking for in-app billing 3 support.");
					int response = mService.isBillingSupported(3, packageName,
							ITEM_TYPE_INAPP);
					if (response != BILLING_RESPONSE_RESULT_OK) {
						if (listener != null)
							listener.onIabSetupFinished(new IabResult(response,
									"Error checking for billing v3 support."));
						return;
					}
					logDebug("In-app billing version 3 supported for "
							+ packageName);
					mSetupDone = true;
				} catch (RemoteException e) {
					if (listener != null) {
						listener.onIabSetupFinished(new IabResult(
								IABHELPER_REMOTE_EXCEPTION,
								"RemoteException while setting up in-app billing."));
					}
					e.printStackTrace();
				} catch (Exception e) {
					/**
					 * 20130402 version 1.2.6, fix for app crash, inapp version
					 * 3,
					 * */
					if (listener != null) {
						listener.onIabSetupFinished(new IabResult(
								IABHELPER_REMOTE_EXCEPTION,
								"RemoteException while setting up in-app billing."));
					}
					e.printStackTrace();

				}

				if (listener != null) {
					listener.onIabSetupFinished(new IabResult(
							BILLING_RESPONSE_RESULT_OK, "Setup successful."));
				}
			}
		};
		mContext.bindService(new Intent(
				"com.android.vending.billing.InAppBillingService.BIND"),
				mServiceConn, Context.BIND_AUTO_CREATE);
	}

	/**
	 * Dispose of object, releasing resources. It's very important to call this
	 * method when you are done with this object. It will release any resources
	 * used by it such as service connections. Naturally, once the object is
	 * disposed of, it can't be used again.
	 */
	public void dispose() {
		logDebug("Disposing.");
		mSetupDone = false;
		if (mServiceConn != null) {
			logDebug("Unbinding from service.");
			if (mContext != null)
				mContext.unbindService(mServiceConn);
			mServiceConn = null;
			mService = null;
			mPurchaseListener = null;
		}
	}

	/**
	 * Callback that notifies when a purchase is finished.
	 */
	public interface OnIabPurchaseFinishedListener {
		/**
		 * Called to notify that an in-app purchase finished. If the purchase
		 * was successful, then the sku parameter specifies which item was
		 * purchased. If the purchase failed, the sku and extraData parameters
		 * may or may not be null, depending on how far the purchase process
		 * went.
		 * 
		 * @param result
		 *            The result of the purchase.
		 * @param info
		 *            The purchase information (null if purchase failed)
		 */
		public void onIabPurchaseFinished(IabResult result, Purchase info);
	}

	// The listener registered on launchPurchaseFlow, which we have to call back
	// when
	// the purchase finishes
	OnIabPurchaseFinishedListener mPurchaseListener;

	/**
	 * Same as calling
	 * {@link #launchPurchaseFlow(Activity, String, int, OnIabPurchaseFinishedListener, String)}
	 * with null as extraData.
	 */
	public void launchPurchaseFlow(Activity act, String sku, int requestCode,
			OnIabPurchaseFinishedListener listener) {
		launchPurchaseFlow(act, sku, requestCode, listener, "");
	}

	/**
	 * Initiate the UI flow for an in-app purchase. Call this method to initiate
	 * an in-app purchase, which will involve bringing up the Google Play
	 * screen. The calling activity will be paused while the user interacts with
	 * Google Play, and the result will be delivered via the activity's
	 * {@link android.app.Activity#onActivityResult} method, at which point you
	 * must call this object's {@link #handleActivityResult} method to continue
	 * the purchase flow. This method MUST be called from the UI thread of the
	 * Activity.
	 * 
	 * @param act
	 *            The calling activity.
	 * @param sku
	 *            The sku of the item to purchase.
	 * @param requestCode
	 *            A request code (to differentiate from other responses -- as in
	 *            {@link android.app.Activity#startActivityForResult}).
	 * @param listener
	 *            The listener to notify when the purchase process finishes
	 * @param extraData
	 *            Extra data (developer payload), which will be returned with
	 *            the purchase data when the purchase completes. This extra data
	 *            will be permanently bound to that purchase and will always be
	 *            returned when the purchase is queried.
	 */
	public void launchPurchaseFlow(Activity act, String sku, int requestCode,
			OnIabPurchaseFinishedListener listener, String extraData) {
		checkSetupDone("launchPurchaseFlow");
		flagStartAsync("launchPurchaseFlow");
		IabResult result;

		try {
			logDebug("Constructing buy intent for " + sku);
			Bundle buyIntentBundle = mService.getBuyIntent(3,
					mContext.getPackageName(), sku, ITEM_TYPE_INAPP, extraData);
			int response = getResponseCodeFromBundle(buyIntentBundle);
			if (response != BILLING_RESPONSE_RESULT_OK) {
				logError("Unable to buy item, Error response: "
						+ getResponseDesc(response));

				result = new IabResult(response, "Unable to buy item");
				if (listener != null)
					listener.onIabPurchaseFinished(result, null);
			}

			PendingIntent pendingIntent = buyIntentBundle
					.getParcelable(RESPONSE_BUY_INTENT);
			logDebug("Launching buy intent for " + sku + ". Request code: "
					+ requestCode);
			mRequestCode = requestCode;
			mPurchaseListener = listener;
			act.startIntentSenderForResult(pendingIntent.getIntentSender(),
					requestCode, new Intent(), Integer.valueOf(0),
					Integer.valueOf(0), Integer.valueOf(0));
		} catch (SendIntentException e) {
			logError("SendIntentException while launching purchase flow for sku "
					+ sku);
			e.printStackTrace();

			result = new IabResult(IABHELPER_SEND_INTENT_FAILED,
					"Failed to send intent.");
			if (listener != null)
				listener.onIabPurchaseFinished(result, null);
		} catch (RemoteException e) {
			logError("RemoteException while launching purchase flow for sku "
					+ sku);
			e.printStackTrace();

			result = new IabResult(IABHELPER_REMOTE_EXCEPTION,
					"Remote exception while starting purchase flow");
			if (listener != null)
				listener.onIabPurchaseFinished(result, null);
		}
	}

/**
	 * Handles an activity result that's part of the purchase flow in in-app
	 * billing. If you are calling {@link #launchPurchaseFlow}, then you must
	 * call this method from your Activity's {@link android.app.Activity
	 * @onActivityResult} method. This method MUST be called from the UI thread
	 * of the Activity.
	 * 
	 * @param requestCode
	 *            The requestCode as you received it.
	 * @param resultCode
	 *            The resultCode as you received it.
	 * @param data
	 *            The data (Intent) as you received it.
	 * @return Returns true if the result was related to a purchase flow and was
	 *         handled; false if the result was not related to a purchase, in
	 *         which case you should handle it normally.
	 */
	public boolean handleActivityResult(int requestCode, int resultCode,
			Intent data) {
		IabResult result;
		if (requestCode != mRequestCode)
			return false;

		checkSetupDone("handleActivityResult");

		// end of async purchase operation
		flagEndAsync();

		if (data == null) {
			logError("Null data in IAB activity result.");
			result = new IabResult(IABHELPER_BAD_RESPONSE,
					"Null data in IAB result");
			if (mPurchaseListener != null)
				mPurchaseListener.onIabPurchaseFinished(result, null);
			return true;
		}

		int responseCode = getResponseCodeFromIntent(data);
		String purchaseData = data.getStringExtra(RESPONSE_INAPP_PURCHASE_DATA);
		String dataSignature = data.getStringExtra(RESPONSE_INAPP_SIGNATURE);

		if (resultCode == Activity.RESULT_OK
				&& responseCode == BILLING_RESPONSE_RESULT_OK) {
			logDebug("Successful resultcode from purchase activity.");
			logDebug("Purchase data: " + purchaseData);
			logDebug("Data signature: " + dataSignature);
			logDebug("Extras: " + data.getExtras());

			if (purchaseData == null || dataSignature == null) {
				logError("BUG: either purchaseData or dataSignature is null.");
				logDebug("Extras: " + data.getExtras().toString());
				result = new IabResult(IABHELPER_UNKNOWN_ERROR,
						"IAB returned null purchaseData or dataSignature");
				if (mPurchaseListener != null)
					mPurchaseListener.onIabPurchaseFinished(result, null);
				return true;
			}

			Purchase purchase = null;
			try {
				purchase = new Purchase(purchaseData, dataSignature);
				String sku = purchase.getSku();

				// Verify signature
				if (!Security.verifyPurchase(mSignatureBase64, purchaseData,
						dataSignature)) {
					logError("Purchase signature verification FAILED for sku "
							+ sku);
					result = new IabResult(IABHELPER_VERIFICATION_FAILED,
							"Signature verification failed for sku " + sku);
					if (mPurchaseListener != null)
						mPurchaseListener.onIabPurchaseFinished(result,
								purchase);
					return true;
				}
				logDebug("Purchase signature successfully verified.");
			} catch (JSONException e) {
				logError("Failed to parse purchase data.");
				e.printStackTrace();
				result = new IabResult(IABHELPER_BAD_RESPONSE,
						"Failed to parse purchase data.");
				if (mPurchaseListener != null)
					mPurchaseListener.onIabPurchaseFinished(result, null);
				return true;
			}

			if (mPurchaseListener != null) {
				mPurchaseListener.onIabPurchaseFinished(new IabResult(
						BILLING_RESPONSE_RESULT_OK, "Success"), purchase);
			}
		} else if (resultCode == Activity.RESULT_OK) {
			// result code was OK, but in-app billing response was not OK.
			logDebug("Result code was OK but in-app billing response was not OK: "
					+ getResponseDesc(responseCode));
			if (mPurchaseListener != null) {
				result = new IabResult(responseCode,
						"Problem purchashing item.");
				mPurchaseListener.onIabPurchaseFinished(result, null);
			}
		} else if (resultCode == Activity.RESULT_CANCELED) {
			logDebug("Purchase canceled - Response: "
					+ getResponseDesc(responseCode));
			result = new IabResult(IABHELPER_USER_CANCELLED, "User canceled.");
			if (mPurchaseListener != null)
				mPurchaseListener.onIabPurchaseFinished(result, null);
		} else {
			logError("Purchase failed. Result code: "
					+ Integer.toString(resultCode) + ". Response: "
					+ getResponseDesc(responseCode));
			result = new IabResult(IABHELPER_UNKNOWN_PURCHASE_RESPONSE,
					"Unknown purchase response.");
			if (mPurchaseListener != null)
				mPurchaseListener.onIabPurchaseFinished(result, null);
		}
		return true;
	}

	/**
	 * Queries the inventory. This will query all owned items from the server,
	 * as well as information on additional skus, if specified. This method may
	 * block or take long to execute. Do not call from a UI thread. For that,
	 * use the non-blocking version {@link #refreshInventoryAsync}.
	 * 
	 * @param querySkuDetails
	 *            if true, SKU details (price, description, etc) will be queried
	 *            as well as purchase information.
	 * @param moreSkus
	 *            additional skus to query information on, regardless of
	 *            ownership. Ignored if null or if querySkuDetails is false.
	 * @throws IabException
	 *             if a problem occurs while refreshing the inventory.
	 */
	public Inventory queryInventory(boolean querySkuDetails,
			List<String> moreSkus) throws IabException {
		checkSetupDone("queryInventory");
		try {
			Inventory inv = new Inventory();
			int r = queryPurchases(inv);
			if (r != BILLING_RESPONSE_RESULT_OK) {
				throw new IabException(r,
						"Error refreshing inventory (querying owned items).");
			}

			if (querySkuDetails) {
				r = querySkuDetails(inv, moreSkus);
				if (r != BILLING_RESPONSE_RESULT_OK) {
					throw new IabException(r,
							"Error refreshing inventory (querying prices of items).");
				}
			}
			return inv;
		} catch (RemoteException e) {
			throw new IabException(IABHELPER_REMOTE_EXCEPTION,
					"Remote exception while refreshing inventory.", e);
		} catch (JSONException e) {
			throw new IabException(IABHELPER_BAD_RESPONSE,
					"Error parsing JSON response while refreshing inventory.",
					e);
		}
	}

	/**
	 * Listener that notifies when an inventory query operation completes.
	 */
	public interface QueryInventoryFinishedListener {
		/**
		 * Called to notify that an inventory query operation completed.
		 * 
		 * @param result
		 *            The result of the operation.
		 * @param inv
		 *            The inventory.
		 */
		public void onQueryInventoryFinished(IabResult result, Inventory inv);
	}

	/**
	 * Asynchronous wrapper for inventory query. This will perform an inventory
	 * query as described in {@link #queryInventory}, but will do so
	 * asynchronously and call back the specified listener upon completion. This
	 * method is safe to call from a UI thread.
	 * 
	 * @param querySkuDetails
	 *            as in {@link #queryInventory}
	 * @param moreSkus
	 *            as in {@link #queryInventory}
	 * @param listener
	 *            The listener to notify when the refresh operation completes.
	 */
	public void queryInventoryAsync(final boolean querySkuDetails,
			final List<String> moreSkus,
			final QueryInventoryFinishedListener listener) {
		final Handler handler = new Handler();
		checkSetupDone("queryInventory");
		flagStartAsync("refresh inventory");
		(new Thread(new Runnable() {
			public void run() {
				IabResult result = new IabResult(BILLING_RESPONSE_RESULT_OK,
						"Inventory refresh successful.");
				Inventory inv = null;
				try {
					inv = queryInventory(querySkuDetails, moreSkus);
				} catch (IabException ex) {
					result = ex.getResult();
				}

				flagEndAsync();

				final IabResult result_f = result;
				final Inventory inv_f = inv;
				handler.post(new Runnable() {
					public void run() {
						listener.onQueryInventoryFinished(result_f, inv_f);
					}
				});
			}
		})).start();
	}

	public void queryInventoryAsync(QueryInventoryFinishedListener listener) {
		queryInventoryAsync(true, null, listener);
	}

	public void queryInventoryAsync(boolean querySkuDetails,
			QueryInventoryFinishedListener listener) {
		queryInventoryAsync(querySkuDetails, null, listener);
	}

	/**
	 * Consumes a given in-app product. Consuming can only be done on an item
	 * that's owned, and as a result of consumption, the user will no longer own
	 * it. This method may block or take long to return. Do not call from the UI
	 * thread. For that, see {@link #consumeAsync}.
	 * 
	 * @param itemInfo
	 *            The PurchaseInfo that represents the item to consume.
	 * @throws IabException
	 *             if there is a problem during consumption.
	 */
	void consume(Purchase itemInfo) throws IabException {
		checkSetupDone("consume");
		try {
			String token = itemInfo.getToken();
			String sku = itemInfo.getSku();
			if (token == null || token.equals("")) {
				logError("Can't consume " + sku + ". No token.");
				throw new IabException(IABHELPER_MISSING_TOKEN,
						"PurchaseInfo is missing token for sku: " + sku + " "
								+ itemInfo);
			}

			logDebug("Consuming sku: " + sku + ", token: " + token);
			int response = mService.consumePurchase(3,
					mContext.getPackageName(), token);
			if (response == BILLING_RESPONSE_RESULT_OK) {
				logDebug("Successfully consumed sku: " + sku);
			} else {
				logDebug("Error consuming consuming sku " + sku + ". "
						+ getResponseDesc(response));
				throw new IabException(response, "Error consuming sku " + sku);
			}
		} catch (RemoteException e) {
			throw new IabException(IABHELPER_REMOTE_EXCEPTION,
					"Remote exception while consuming. PurchaseInfo: "
							+ itemInfo, e);
		}
	}

	/**
	 * Callback that notifies when a consumption operation finishes.
	 */
	public interface OnConsumeFinishedListener {
		/**
		 * Called to notify that a consumption has finished.
		 * 
		 * @param purchase
		 *            The purchase that was (or was to be) consumed.
		 * @param result
		 *            The result of the consumption operation.
		 */
		public void onConsumeFinished(Purchase purchase, IabResult result);
	}

	/**
	 * Callback that notifies when a multi-item consumption operation finishes.
	 */
	public interface OnConsumeMultiFinishedListener {
		/**
		 * Called to notify that a consumption of multiple items has finished.
		 * 
		 * @param purchases
		 *            The purchases that were (or were to be) consumed.
		 * @param results
		 *            The results of each consumption operation, corresponding
		 *            to each sku.
		 */
		public void onConsumeMultiFinished(List<Purchase> purchases,
				List<IabResult> results);
	}

	/**
	 * Asynchronous wrapper to item consumption. Works like {@link #consume},
	 * but performs the consumption in the background and notifies completion
	 * through the provided listener. This method is safe to call from a UI
	 * thread.
	 * 
	 * @param purchase
	 *            The purchase to be consumed.
	 * @param listener
	 *            The listener to notify when the consumption operation
	 *            finishes.
	 */
	public void consumeAsync(Purchase purchase,
			OnConsumeFinishedListener listener) {
		checkSetupDone("consume");
		List<Purchase> purchases = new ArrayList<Purchase>();
		purchases.add(purchase);
		consumeAsyncInternal(purchases, listener, null);
	}

	/**
	 * Same as {@link consumeAsync}, but for multiple items at once.
	 * 
	 * @param purchases
	 *            The list of PurchaseInfo objects representing the purchases to
	 *            consume.
	 * @param listener
	 *            The listener to notify when the consumption operation
	 *            finishes.
	 */
	public void consumeAsync(List<Purchase> purchases,
			OnConsumeMultiFinishedListener listener) {
		checkSetupDone("consume");
		consumeAsyncInternal(purchases, null, listener);
	}

	/**
	 * Returns a human-readable description for the given response code.
	 * 
	 * @param code
	 *            The response code
	 * @return A human-readable string explaining the result code. It also
	 *         includes the result code numerically.
	 */
	public static String getResponseDesc(int code) {
		String[] iab_msgs = ("0:OK/1:User Canceled/2:Unknown/"
				+ "3:Billing Unavailable/4:Item unavailable/"
				+ "5:Developer Error/6:Error/7:Item Already Owned/"
				+ "8:Item not owned").split("/");
		String[] iabhelper_msgs = ("0:OK/-1001:Remote exception during initialization/"
				+ "-1002:Bad response received/"
				+ "-1003:Purchase signature verification failed/"
				+ "-1004:Send intent failed/"
				+ "-1005:User cancelled/"
				+ "-1006:Unknown purchase response/"
				+ "-1007:Missing token/"
				+ "-1008:Unknown error").split("/");

		if (code <= IABHELPER_ERROR_BASE) {
			int index = IABHELPER_ERROR_BASE - code;
			if (index >= 0 && index < iabhelper_msgs.length)
				return iabhelper_msgs[index];
			else
				return String.valueOf(code) + ":Unknown IAB Helper Error";
		} else if (code < 0 || code >= iab_msgs.length)
			return String.valueOf(code) + ":Unknown";
		else
			return iab_msgs[code];
	}

	// Checks that setup was done; if not, throws an exception.
	void checkSetupDone(String operation) {
		if (!mSetupDone) {
			logError("Illegal state for operation (" + operation
					+ "): IAB helper is not set up.");
			throw new IllegalStateException(
					"IAB helper is not set up. Can't perform operation: "
							+ operation);
		}
	}

	// Workaround to bug where sometimes response codes come as Long instead of
	// Integer
	int getResponseCodeFromBundle(Bundle b) {
		// 20130402 check bug null
		//
		//

		// if (b == null) {
		// logDebug("Bundle with null response code, assuming OK (known issue)");
		// return BILLING_RESPONSE_RESULT_OK;
		// }

		Object o = b.get(RESPONSE_CODE);
		if (o == null) {
			logDebug("Bundle with null response code, assuming OK (known issue)");
			return BILLING_RESPONSE_RESULT_OK;
		} else if (o instanceof Integer)
			return ((Integer) o).intValue();
		else if (o instanceof Long)
			return (int) ((Long) o).longValue();
		else {
			logError("Unexpected type for bundle response code.");
			logError(o.getClass().getName());
			throw new RuntimeException(
					"Unexpected type for bundle response code: "
							+ o.getClass().getName());
		}
	}

	// Workaround to bug where sometimes response codes come as Long instead of
	// Integer
	int getResponseCodeFromIntent(Intent i) {
		Object o = i.getExtras().get(RESPONSE_CODE);
		if (o == null) {
			logError("Intent with no response code, assuming OK (known issue)");
			return BILLING_RESPONSE_RESULT_OK;
		} else if (o instanceof Integer)
			return ((Integer) o).intValue();
		else if (o instanceof Long)
			return (int) ((Long) o).longValue();
		else {
			logError("Unexpected type for intent response code.");
			logError(o.getClass().getName());
			throw new RuntimeException(
					"Unexpected type for intent response code: "
							+ o.getClass().getName());
		}
	}

	void flagStartAsync(String operation) {
		if (mAsyncInProgress)
			throw new IllegalStateException("Can't start async operation ("
					+ operation + ") because another async operation("
					+ mAsyncOperation + ") is in progress.");
		mAsyncOperation = operation;
		mAsyncInProgress = true;
		logDebug("Starting async operation: " + operation);
	}

	void flagEndAsync() {
		logDebug("Ending async operation: " + mAsyncOperation);
		mAsyncOperation = "";
		mAsyncInProgress = false;
	}

	int queryPurchases(Inventory inv) throws JSONException, RemoteException {
		// Query purchases
		logDebug("Querying owned items...");
		logDebug("Package name: " + mContext.getPackageName());
		boolean hasMore = true;
		boolean verificationFailed = false;
		String continueToken = null;

		do {
			logDebug("Calling getPurchases with continuation token: "
					+ continueToken);
			Bundle ownedItems = mService.getPurchases(3,
					mContext.getPackageName(), ITEM_TYPE_INAPP, continueToken);

			int response = getResponseCodeFromBundle(ownedItems);
			logDebug("Owned items response: " + String.valueOf(response));
			if (response != BILLING_RESPONSE_RESULT_OK) {
				logDebug("getPurchases() failed: " + getResponseDesc(response));
				return response;
			}
			if (!ownedItems.containsKey(RESPONSE_INAPP_ITEM_LIST)
					|| !ownedItems
							.containsKey(RESPONSE_INAPP_PURCHASE_DATA_LIST)
					|| !ownedItems.containsKey(RESPONSE_INAPP_SIGNATURE_LIST)) {
				logError("Bundle returned from getPurchases() doesn't contain required fields.");
				return IABHELPER_BAD_RESPONSE;
			}

			ArrayList<String> ownedSkus = ownedItems
					.getStringArrayList(RESPONSE_INAPP_ITEM_LIST);
			ArrayList<String> purchaseDataList = ownedItems
					.getStringArrayList(RESPONSE_INAPP_PURCHASE_DATA_LIST);
			ArrayList<String> signatureList = ownedItems
					.getStringArrayList(RESPONSE_INAPP_SIGNATURE_LIST);

			for (int i = 0; i < purchaseDataList.size(); ++i) {
				String purchaseData = purchaseDataList.get(i);
				String signature = signatureList.get(i);
				String sku = ownedSkus.get(i);
				if (Security.verifyPurchase(mSignatureBase64, purchaseData,
						signature)) {
					logDebug("Sku is owned: " + sku);
					Purchase purchase = new Purchase(purchaseData, signature);

					if (TextUtils.isEmpty(purchase.getToken())) {
						logWarn("BUG: empty/null token!");
						logDebug("Purchase data: " + purchaseData);
					}

					// Record ownership and token
					inv.addPurchase(purchase);
				} else {
					logWarn("Purchase signature verification **FAILED**. Not adding item.");
					logDebug("   Purchase data: " + purchaseData);
					logDebug("   Signature: " + signature);
					verificationFailed = true;
				}
			}

			continueToken = ownedItems.getString(INAPP_CONTINUATION_TOKEN);
			logDebug("Continuation token: " + continueToken);
		} while (!TextUtils.isEmpty(continueToken));

		return verificationFailed ? IABHELPER_VERIFICATION_FAILED
				: BILLING_RESPONSE_RESULT_OK;
	}

	int querySkuDetails(Inventory inv, List<String> moreSkus)
			throws RemoteException, JSONException {
		logDebug("Querying SKU details.");
		ArrayList<String> skuList = new ArrayList<String>();
		skuList.addAll(inv.getAllOwnedSkus());
		if (moreSkus != null)
			skuList.addAll(moreSkus);

		if (skuList.size() == 0) {
			logDebug("queryPrices: nothing to do because there are no SKUs.");
			return BILLING_RESPONSE_RESULT_OK;
		}

		Bundle querySkus = new Bundle();
		querySkus.putStringArrayList(GET_SKU_DETAILS_ITEM_LIST, skuList);
		Bundle skuDetails = mService.getSkuDetails(3,
				mContext.getPackageName(), ITEM_TYPE_INAPP, querySkus);

		if (!skuDetails.containsKey(RESPONSE_GET_SKU_DETAILS_LIST)) {
			int response = getResponseCodeFromBundle(skuDetails);
			if (response != BILLING_RESPONSE_RESULT_OK) {
				logDebug("getSkuDetails() failed: " + getResponseDesc(response));
				return response;
			} else {
				logError("getSkuDetails() returned a bundle with neither an error nor a detail list.");
				return IABHELPER_BAD_RESPONSE;
			}
		}

		ArrayList<String> responseList = skuDetails
				.getStringArrayList(RESPONSE_GET_SKU_DETAILS_LIST);

		for (String thisResponse : responseList) {
			SkuDetails d = new SkuDetails(thisResponse);
			logDebug("Got sku details: " + d);
			inv.addSkuDetails(d);
		}
		return BILLING_RESPONSE_RESULT_OK;
	}

	void consumeAsyncInternal(final List<Purchase> purchases,
			final OnConsumeFinishedListener singleListener,
			final OnConsumeMultiFinishedListener multiListener) {
		final Handler handler = new Handler();
		flagStartAsync("consume");
		(new Thread(new Runnable() {
			public void run() {
				final List<IabResult> results = new ArrayList<IabResult>();
				for (Purchase purchase : purchases) {
					try {
						consume(purchase);
						results.add(new IabResult(BILLING_RESPONSE_RESULT_OK,
								"Successful consume of sku "
										+ purchase.getSku()));
					} catch (IabException ex) {
						results.add(ex.getResult());
					}
				}

				flagEndAsync();
				if (singleListener != null) {
					handler.post(new Runnable() {
						public void run() {
							singleListener.onConsumeFinished(purchases.get(0),
									results.get(0));
						}
					});
				}
				if (multiListener != null) {
					handler.post(new Runnable() {
						public void run() {
							multiListener.onConsumeMultiFinished(purchases,
									results);
						}
					});
				}
			}
		})).start();
	}

	void logDebug(String msg) {
		if (mDebugLog)
			Log.d(mDebugTag, msg);
	}

	void logError(String msg) {
		Log.e(mDebugTag, "In-app billing error: " + msg);
	}

	void logWarn(String msg) {
		Log.w(mDebugTag, "In-app billing warning: " + msg);
	}
}

// ===============================================================================
// IabException
// ===============================================================================
class IabException extends Exception {
	IabResult mResult;

	public IabException(IabResult r) {
		this(r, null);
	}

	public IabException(int response, String message) {
		this(new IabResult(response, message));
	}

	public IabException(IabResult r, Exception cause) {
		super(r.getMessage(), cause);
		mResult = r;
	}

	public IabException(int response, String message, Exception cause) {
		this(new IabResult(response, message), cause);
	}

	/** Returns the IAB result (error) that this exception signals. */
	public IabResult getResult() {
		return mResult;
	}
}

// ===============================================================================
// IabResult
// ===============================================================================
/**
 * Represents the result of an in-app billing operation. A result is composed of
 * a response code (an integer) and possibly a message (String). You can get
 * those by calling {@link #getResponse} and {@link #getMessage()},
 * respectively. You can also inquire whether a result is a success or a failure
 * by calling {@link #isSuccess()} and {@link #isFailure()}.
 */
class IabResult {
	int mResponse;
	String mMessage;

	public IabResult(int response, String message) {
		mResponse = response;
		if (message == null || message.trim().length() == 0) {
			mMessage = IabHelper.getResponseDesc(response);
		} else {
			mMessage = message + " (response: "
					+ IabHelper.getResponseDesc(response) + ")";
		}
	}

	public int getResponse() {
		return mResponse;
	}

	public String getMessage() {
		return mMessage;
	}

	public boolean isSuccess() {
		return mResponse == IabHelper.BILLING_RESPONSE_RESULT_OK;
	}

	public boolean isFailure() {
		return !isSuccess();
	}

	public String toString() {
		return "IabResult: " + getMessage();
	}
}

// ===============================================================================
// Inventory
// ===============================================================================
/**
 * Represents a block of information about in-app items. An Inventory is
 * returned by such methods as {@link IabHelper#queryInventory}.
 */
class Inventory {
	Map<String, SkuDetails> mSkuMap = new HashMap<String, SkuDetails>();
	Map<String, Purchase> mPurchaseMap = new HashMap<String, Purchase>();

	Inventory() {
	}

	/** Returns the listing details for an in-app product. */
	public SkuDetails getSkuDetails(String sku) {
		return mSkuMap.get(sku);
	}

	/**
	 * Returns purchase information for a given product, or null if there is no
	 * purchase.
	 */
	public Purchase getPurchase(String sku) {
		return mPurchaseMap.get(sku);
	}

	/** Returns whether or not there exists a purchase of the given product. */
	public boolean hasPurchase(String sku) {
		return mPurchaseMap.containsKey(sku);
	}

	/** Return whether or not details about the given product are available. */
	public boolean hasDetails(String sku) {
		return mSkuMap.containsKey(sku);
	}

	/**
	 * Erase a purchase (locally) from the inventory, given its product ID. This
	 * just modifies the Inventory object locally and has no effect on the
	 * server! This is useful when you have an existing Inventory object which
	 * you know to be up to date, and you have just consumed an item
	 * successfully, which means that erasing its purchase data from the
	 * Inventory you already have is quicker than querying for a new Inventory.
	 */
	public void erasePurchase(String sku) {
		if (mPurchaseMap.containsKey(sku))
			mPurchaseMap.remove(sku);
	}

	/** Returns a list of all owned product IDs. */
	List<String> getAllOwnedSkus() {
		return new ArrayList<String>(mPurchaseMap.keySet());
	}

	/** Returns a list of all purchases. */
	List<Purchase> getAllPurchases() {
		return new ArrayList<Purchase>(mPurchaseMap.values());
	}

	void addSkuDetails(SkuDetails d) {
		mSkuMap.put(d.getSku(), d);
	}

	void addPurchase(Purchase p) {
		mPurchaseMap.put(p.getSku(), p);
	}
}

// ===============================================================================
// Purchase
// ===============================================================================

/**
 * Represents an in-app billing purchase.
 */
class Purchase {
	String mOrderId;
	String mPackageName;
	String mSku;
	long mPurchaseTime;
	int mPurchaseState;
	String mDeveloperPayload;
	String mToken;
	String mOriginalJson;
	String mSignature;

	public Purchase(String jsonPurchaseInfo, String signature)
			throws JSONException {
		mOriginalJson = jsonPurchaseInfo;
		JSONObject o = new JSONObject(mOriginalJson);
		mOrderId = o.optString("orderId");
		mPackageName = o.optString("packageName");
		mSku = o.optString("productId");
		mPurchaseTime = o.optLong("purchaseTime");
		mPurchaseState = o.optInt("purchaseState");
		mDeveloperPayload = o.optString("developerPayload");
		mToken = o.optString("token", o.optString("purchaseToken"));
		mSignature = signature;
	}

	public String getOrderId() {
		return mOrderId;
	}

	public String getPackageName() {
		return mPackageName;
	}

	public String getSku() {
		return mSku;
	}

	public long getPurchaseTime() {
		return mPurchaseTime;
	}

	public int getPurchaseState() {
		return mPurchaseState;
	}

	public String getDeveloperPayload() {
		return mDeveloperPayload;
	}

	public String getToken() {
		return mToken;
	}

	public String getOriginalJson() {
		return mOriginalJson;
	}

	public String getSignature() {
		return mSignature;
	}

	@Override
	public String toString() {
		return "PurchaseInfo:" + mOriginalJson;
	}
}

// ===============================================================================
// SkuDetails
// ===============================================================================

/**
 * Represents an in-app product's listing details.
 */
class SkuDetails {
	String mSku;
	String mType;
	String mPrice;
	String mTitle;
	String mDescription;
	String mJson;

	public SkuDetails(String jsonSkuDetails) throws JSONException {
		mJson = jsonSkuDetails;
		JSONObject o = new JSONObject(mJson);
		mSku = o.optString("productId");
		mType = o.optString("type");
		mPrice = o.optString("price");
		mTitle = o.optString("title");
		mDescription = o.optString("description");
	}

	public String getSku() {
		return mSku;
	}

	public String getType() {
		return mType;
	}

	public String getPrice() {
		return mPrice;
	}

	public String getTitle() {
		return mTitle;
	}

	public String getDescription() {
		return mDescription;
	}

	@Override
	public String toString() {
		return "SkuDetails:" + mJson;
	}
}

// ===============================================================================
// Security
// ===============================================================================
/**
 * Security-related methods. For a secure implementation, all of this code
 * should be implemented on a server that communicates with the application on
 * the device. For the sake of simplicity and clarity of this example, this code
 * is included here and is executed on the device. If you must verify the
 * purchases on the phone, you should obfuscate this code to make it harder for
 * an attacker to replace the code with stubs that treat all purchases as
 * verified.
 */
class Security {
	private static final String TAG = "IABUtil/Security";

	private static final String KEY_FACTORY_ALGORITHM = "RSA";
	private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

	/**
	 * Verifies that the data was signed with the given signature, and returns
	 * the verified purchase. The data is in JSON format and signed with a
	 * private key. The data also contains the {@link PurchaseState} and product
	 * ID of the purchase.
	 * 
	 * @param base64PublicKey
	 *            the base64-encoded public key to use for verifying.
	 * @param signedData
	 *            the signed JSON string (signed, not encrypted)
	 * @param signature
	 *            the signature for the data, signed with the private key
	 */
	public static boolean verifyPurchase(String base64PublicKey,
			String signedData, String signature) {
		if (signedData == null) {
			Log.e(TAG, "data is null");
			return false;
		}

		boolean verified = false;
		if (!TextUtils.isEmpty(signature)) {
			PublicKey key = Security.generatePublicKey(base64PublicKey);
			verified = Security.verify(key, signedData, signature);
			if (!verified) {
				Log.w(TAG, "signature does not match data.");
				return false;
			}
		}
		return true;
	}

	/**
	 * Generates a PublicKey instance from a string containing the
	 * Base64-encoded public key.
	 * 
	 * @param encodedPublicKey
	 *            Base64-encoded public key
	 * @throws IllegalArgumentException
	 *             if encodedPublicKey is invalid
	 */
	public static PublicKey generatePublicKey(String encodedPublicKey) {
		try {
			byte[] decodedKey = Base64.decode(encodedPublicKey);
			KeyFactory keyFactory = KeyFactory
					.getInstance(KEY_FACTORY_ALGORITHM);
			return keyFactory
					.generatePublic(new X509EncodedKeySpec(decodedKey));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeySpecException e) {
			Log.e(TAG, "Invalid key specification.");
			throw new IllegalArgumentException(e);
		} catch (Base64DecoderException e) {
			Log.e(TAG, "Base64 decoding failed.");
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Verifies that the signature from the server matches the computed
	 * signature on the data. Returns true if the data is correctly signed.
	 * 
	 * @param publicKey
	 *            public key associated with the developer account
	 * @param signedData
	 *            signed data from server
	 * @param signature
	 *            server signature
	 * @return true if the data and signature match
	 */
	public static boolean verify(PublicKey publicKey, String signedData,
			String signature) {
		Signature sig;
		try {
			sig = Signature.getInstance(SIGNATURE_ALGORITHM);
			sig.initVerify(publicKey);
			sig.update(signedData.getBytes());
			if (!sig.verify(Base64.decode(signature))) {
				Log.e(TAG, "Signature verification failed.");
				return false;
			}
			return true;
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, "NoSuchAlgorithmException.");
		} catch (InvalidKeyException e) {
			Log.e(TAG, "Invalid key specification.");
		} catch (SignatureException e) {
			Log.e(TAG, "Signature exception.");
		} catch (Base64DecoderException e) {
			Log.e(TAG, "Base64 decoding failed.");
		}
		return false;
	}
}

class Base64DecoderException extends Exception {
	public Base64DecoderException() {
		super();
	}

	public Base64DecoderException(String s) {
		super(s);
	}

	private static final long serialVersionUID = 1L;
}

// This code was converted from code at http://iharder.sourceforge.net/base64/
// Lots of extraneous features were removed.
/*
 * The original code said: <p> I am placing this code in the Public Domain. Do
 * with it as you will. This software comes with no guarantees or warranties but
 * with plenty of well-wishing instead! Please visit <a
 * href="http://iharder.net/xmlizable">http://iharder.net/xmlizable</a>
 * periodically to check for updates or to contribute improvements. </p>
 * 
 * @author Robert Harder
 * 
 * @author rharder@usa.net
 * 
 * @version 1.3
 */

/**
 * Base64 converter class. This code is not a complete MIME encoder; it simply
 * converts binary data to base64 data and back.
 * 
 * <p>
 * Note {@link CharBase64} is a GWT-compatible implementation of this class.
 */
class Base64 {
	/** Specify encoding (value is {@code true}). */
	public final static boolean ENCODE = true;

	/** Specify decoding (value is {@code false}). */
	public final static boolean DECODE = false;

	/** The equals sign (=) as a byte. */
	private final static byte EQUALS_SIGN = (byte) '=';

	/** The new line character (\n) as a byte. */
	private final static byte NEW_LINE = (byte) '\n';

	/**
	 * The 64 valid Base64 values.
	 */
	private final static byte[] ALPHABET = { (byte) 'A', (byte) 'B',
			(byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G',
			(byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L',
			(byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P', (byte) 'Q',
			(byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V',
			(byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) 'a',
			(byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f',
			(byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k',
			(byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p',
			(byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u',
			(byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z',
			(byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
			(byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9',
			(byte) '+', (byte) '/' };

	/**
	 * The 64 valid web safe Base64 values.
	 */
	private final static byte[] WEBSAFE_ALPHABET = { (byte) 'A', (byte) 'B',
			(byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G',
			(byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L',
			(byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P', (byte) 'Q',
			(byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V',
			(byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) 'a',
			(byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f',
			(byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k',
			(byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p',
			(byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u',
			(byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z',
			(byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
			(byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9',
			(byte) '-', (byte) '_' };

	/**
	 * Translates a Base64 value to either its 6-bit reconstruction value or a
	 * negative number indicating some other meaning.
	 **/
	private final static byte[] DECODABET = { -9, -9, -9, -9, -9, -9, -9, -9,
			-9, // Decimal
				// 0
				// -
				// 8
			-5, -5, // Whitespace: Tab and Linefeed
			-9, -9, // Decimal 11 - 12
			-5, // Whitespace: Carriage Return
			-9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 14 -
																// 26
			-9, -9, -9, -9, -9, // Decimal 27 - 31
			-5, // Whitespace: Space
			-9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 33 - 42
			62, // Plus sign at decimal 43
			-9, -9, -9, // Decimal 44 - 46
			63, // Slash at decimal 47
			52, 53, 54, 55, 56, 57, 58, 59, 60, 61, // Numbers zero through nine
			-9, -9, -9, // Decimal 58 - 60
			-1, // Equals sign at decimal 61
			-9, -9, -9, // Decimal 62 - 64
			0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, // Letters 'A' through
															// 'N'
			14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, // Letters 'O'
															// through 'Z'
			-9, -9, -9, -9, -9, -9, // Decimal 91 - 96
			26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, // Letters 'a'
																// through 'm'
			39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, // Letters 'n'
																// through 'z'
			-9, -9, -9, -9, -9 // Decimal 123 - 127
	/*
	 * ,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 128 - 139
	 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 140 - 152
	 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 153 - 165
	 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 166 - 178
	 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 179 - 191
	 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 192 - 204
	 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 205 - 217
	 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 218 - 230
	 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 231 - 243
	 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9 // Decimal 244 - 255
	 */
	};

	/** The web safe decodabet */
	private final static byte[] WEBSAFE_DECODABET = { -9, -9, -9, -9, -9, -9,
			-9, -9, -9, // Decimal
						// 0
						// -
						// 8
			-5, -5, // Whitespace: Tab and Linefeed
			-9, -9, // Decimal 11 - 12
			-5, // Whitespace: Carriage Return
			-9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 14 -
																// 26
			-9, -9, -9, -9, -9, // Decimal 27 - 31
			-5, // Whitespace: Space
			-9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 33 - 44
			62, // Dash '-' sign at decimal 45
			-9, -9, // Decimal 46-47
			52, 53, 54, 55, 56, 57, 58, 59, 60, 61, // Numbers zero through nine
			-9, -9, -9, // Decimal 58 - 60
			-1, // Equals sign at decimal 61
			-9, -9, -9, // Decimal 62 - 64
			0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, // Letters 'A' through
															// 'N'
			14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, // Letters 'O'
															// through 'Z'
			-9, -9, -9, -9, // Decimal 91-94
			63, // Underscore '_' at decimal 95
			-9, // Decimal 96
			26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, // Letters 'a'
																// through 'm'
			39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, // Letters 'n'
																// through 'z'
			-9, -9, -9, -9, -9 // Decimal 123 - 127
	/*
	 * ,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 128 - 139
	 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 140 - 152
	 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 153 - 165
	 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 166 - 178
	 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 179 - 191
	 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 192 - 204
	 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 205 - 217
	 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 218 - 230
	 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 231 - 243
	 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9 // Decimal 244 - 255
	 */
	};

	// Indicates white space in encoding
	private final static byte WHITE_SPACE_ENC = -5;
	// Indicates equals sign in encoding
	private final static byte EQUALS_SIGN_ENC = -1;

	/** Defeats instantiation. */
	private Base64() {
	}

	/* ******** E N C O D I N G M E T H O D S ******** */

	/**
	 * Encodes up to three bytes of the array <var>source</var> and writes the
	 * resulting four Base64 bytes to <var>destination</var>. The source and
	 * destination arrays can be manipulated anywhere along their length by
	 * specifying <var>srcOffset</var> and <var>destOffset</var>. This method
	 * does not check to make sure your arrays are large enough to accommodate
	 * <var>srcOffset</var> + 3 for the <var>source</var> array or
	 * <var>destOffset</var> + 4 for the <var>destination</var> array. The
	 * actual number of significant bytes in your array is given by
	 * <var>numSigBytes</var>.
	 * 
	 * @param source
	 *            the array to convert
	 * @param srcOffset
	 *            the index where conversion begins
	 * @param numSigBytes
	 *            the number of significant bytes in your array
	 * @param destination
	 *            the array to hold the conversion
	 * @param destOffset
	 *            the index where output will be put
	 * @param alphabet
	 *            is the encoding alphabet
	 * @return the <var>destination</var> array
	 * @since 1.3
	 */
	private static byte[] encode3to4(byte[] source, int srcOffset,
			int numSigBytes, byte[] destination, int destOffset, byte[] alphabet) {
		// 1 2 3
		// 01234567890123456789012345678901 Bit position
		// --------000000001111111122222222 Array position from threeBytes
		// --------| || || || | Six bit groups to index alphabet
		// >>18 >>12 >> 6 >> 0 Right shift necessary
		// 0x3f 0x3f 0x3f Additional AND

		// Create buffer with zero-padding if there are only one or two
		// significant bytes passed in the array.
		// We have to shift left 24 in order to flush out the 1's that appear
		// when Java treats a value as negative that is cast from a byte to an
		// int.
		int inBuff = (numSigBytes > 0 ? ((source[srcOffset] << 24) >>> 8) : 0)
				| (numSigBytes > 1 ? ((source[srcOffset + 1] << 24) >>> 16) : 0)
				| (numSigBytes > 2 ? ((source[srcOffset + 2] << 24) >>> 24) : 0);

		switch (numSigBytes) {
		case 3:
			destination[destOffset] = alphabet[(inBuff >>> 18)];
			destination[destOffset + 1] = alphabet[(inBuff >>> 12) & 0x3f];
			destination[destOffset + 2] = alphabet[(inBuff >>> 6) & 0x3f];
			destination[destOffset + 3] = alphabet[(inBuff) & 0x3f];
			return destination;
		case 2:
			destination[destOffset] = alphabet[(inBuff >>> 18)];
			destination[destOffset + 1] = alphabet[(inBuff >>> 12) & 0x3f];
			destination[destOffset + 2] = alphabet[(inBuff >>> 6) & 0x3f];
			destination[destOffset + 3] = EQUALS_SIGN;
			return destination;
		case 1:
			destination[destOffset] = alphabet[(inBuff >>> 18)];
			destination[destOffset + 1] = alphabet[(inBuff >>> 12) & 0x3f];
			destination[destOffset + 2] = EQUALS_SIGN;
			destination[destOffset + 3] = EQUALS_SIGN;
			return destination;
		default:
			return destination;
		} // end switch
	} // end encode3to4

	/**
	 * Encodes a byte array into Base64 notation. Equivalent to calling
	 * {@code encodeBytes(source, 0, source.length)}
	 * 
	 * @param source
	 *            The data to convert
	 * @since 1.4
	 */
	public static String encode(byte[] source) {
		return encode(source, 0, source.length, ALPHABET, true);
	}

	/**
	 * Encodes a byte array into web safe Base64 notation.
	 * 
	 * @param source
	 *            The data to convert
	 * @param doPadding
	 *            is {@code true} to pad result with '=' chars if it does not
	 *            fall on 3 byte boundaries
	 */
	public static String encodeWebSafe(byte[] source, boolean doPadding) {
		return encode(source, 0, source.length, WEBSAFE_ALPHABET, doPadding);
	}

	/**
	 * Encodes a byte array into Base64 notation.
	 * 
	 * @param source
	 *            the data to convert
	 * @param off
	 *            offset in array where conversion should begin
	 * @param len
	 *            length of data to convert
	 * @param alphabet
	 *            the encoding alphabet
	 * @param doPadding
	 *            is {@code true} to pad result with '=' chars if it does not
	 *            fall on 3 byte boundaries
	 * @since 1.4
	 */
	public static String encode(byte[] source, int off, int len,
			byte[] alphabet, boolean doPadding) {
		byte[] outBuff = encode(source, off, len, alphabet, Integer.MAX_VALUE);
		int outLen = outBuff.length;

		// If doPadding is false, set length to truncate '='
		// padding characters
		while (doPadding == false && outLen > 0) {
			if (outBuff[outLen - 1] != '=') {
				break;
			}
			outLen -= 1;
		}

		return new String(outBuff, 0, outLen);
	}

	/**
	 * Encodes a byte array into Base64 notation.
	 * 
	 * @param source
	 *            the data to convert
	 * @param off
	 *            offset in array where conversion should begin
	 * @param len
	 *            length of data to convert
	 * @param alphabet
	 *            is the encoding alphabet
	 * @param maxLineLength
	 *            maximum length of one line.
	 * @return the BASE64-encoded byte array
	 */
	public static byte[] encode(byte[] source, int off, int len,
			byte[] alphabet, int maxLineLength) {
		int lenDiv3 = (len + 2) / 3; // ceil(len / 3)
		int len43 = lenDiv3 * 4;
		byte[] outBuff = new byte[len43 // Main 4:3
				+ (len43 / maxLineLength)]; // New lines

		int d = 0;
		int e = 0;
		int len2 = len - 2;
		int lineLength = 0;
		for (; d < len2; d += 3, e += 4) {

			// The following block of code is the same as
			// encode3to4( source, d + off, 3, outBuff, e, alphabet );
			// but inlined for faster encoding (~20% improvement)
			int inBuff = ((source[d + off] << 24) >>> 8)
					| ((source[d + 1 + off] << 24) >>> 16)
					| ((source[d + 2 + off] << 24) >>> 24);
			outBuff[e] = alphabet[(inBuff >>> 18)];
			outBuff[e + 1] = alphabet[(inBuff >>> 12) & 0x3f];
			outBuff[e + 2] = alphabet[(inBuff >>> 6) & 0x3f];
			outBuff[e + 3] = alphabet[(inBuff) & 0x3f];

			lineLength += 4;
			if (lineLength == maxLineLength) {
				outBuff[e + 4] = NEW_LINE;
				e++;
				lineLength = 0;
			} // end if: end of line
		} // end for: each piece of array

		if (d < len) {
			encode3to4(source, d + off, len - d, outBuff, e, alphabet);

			lineLength += 4;
			if (lineLength == maxLineLength) {
				// Add a last newline
				outBuff[e + 4] = NEW_LINE;
				e++;
			}
			e += 4;
		}

		assert (e == outBuff.length);
		return outBuff;
	}

	/* ******** D E C O D I N G M E T H O D S ******** */

	/**
	 * Decodes four bytes from array <var>source</var> and writes the resulting
	 * bytes (up to three of them) to <var>destination</var>. The source and
	 * destination arrays can be manipulated anywhere along their length by
	 * specifying <var>srcOffset</var> and <var>destOffset</var>. This method
	 * does not check to make sure your arrays are large enough to accommodate
	 * <var>srcOffset</var> + 4 for the <var>source</var> array or
	 * <var>destOffset</var> + 3 for the <var>destination</var> array. This
	 * method returns the actual number of bytes that were converted from the
	 * Base64 encoding.
	 * 
	 * 
	 * @param source
	 *            the array to convert
	 * @param srcOffset
	 *            the index where conversion begins
	 * @param destination
	 *            the array to hold the conversion
	 * @param destOffset
	 *            the index where output will be put
	 * @param decodabet
	 *            the decodabet for decoding Base64 content
	 * @return the number of decoded bytes converted
	 * @since 1.3
	 */
	private static int decode4to3(byte[] source, int srcOffset,
			byte[] destination, int destOffset, byte[] decodabet) {
		// Example: Dk==
		if (source[srcOffset + 2] == EQUALS_SIGN) {
			int outBuff = ((decodabet[source[srcOffset]] << 24) >>> 6)
					| ((decodabet[source[srcOffset + 1]] << 24) >>> 12);

			destination[destOffset] = (byte) (outBuff >>> 16);
			return 1;
		} else if (source[srcOffset + 3] == EQUALS_SIGN) {
			// Example: DkL=
			int outBuff = ((decodabet[source[srcOffset]] << 24) >>> 6)
					| ((decodabet[source[srcOffset + 1]] << 24) >>> 12)
					| ((decodabet[source[srcOffset + 2]] << 24) >>> 18);

			destination[destOffset] = (byte) (outBuff >>> 16);
			destination[destOffset + 1] = (byte) (outBuff >>> 8);
			return 2;
		} else {
			// Example: DkLE
			int outBuff = ((decodabet[source[srcOffset]] << 24) >>> 6)
					| ((decodabet[source[srcOffset + 1]] << 24) >>> 12)
					| ((decodabet[source[srcOffset + 2]] << 24) >>> 18)
					| ((decodabet[source[srcOffset + 3]] << 24) >>> 24);

			destination[destOffset] = (byte) (outBuff >> 16);
			destination[destOffset + 1] = (byte) (outBuff >> 8);
			destination[destOffset + 2] = (byte) (outBuff);
			return 3;
		}
	} // end decodeToBytes

	/**
	 * Decodes data from Base64 notation.
	 * 
	 * @param s
	 *            the string to decode (decoded in default encoding)
	 * @return the decoded data
	 * @since 1.4
	 */
	public static byte[] decode(String s) throws Base64DecoderException {
		byte[] bytes = s.getBytes();
		return decode(bytes, 0, bytes.length);
	}

	/**
	 * Decodes data from web safe Base64 notation. Web safe encoding uses '-'
	 * instead of '+', '_' instead of '/'
	 * 
	 * @param s
	 *            the string to decode (decoded in default encoding)
	 * @return the decoded data
	 */
	public static byte[] decodeWebSafe(String s) throws Base64DecoderException {
		byte[] bytes = s.getBytes();
		return decodeWebSafe(bytes, 0, bytes.length);
	}

	/**
	 * Decodes Base64 content in byte array format and returns the decoded byte
	 * array.
	 * 
	 * @param source
	 *            The Base64 encoded data
	 * @return decoded data
	 * @since 1.3
	 * @throws Base64DecoderException
	 */
	public static byte[] decode(byte[] source) throws Base64DecoderException {
		return decode(source, 0, source.length);
	}

	/**
	 * Decodes web safe Base64 content in byte array format and returns the
	 * decoded data. Web safe encoding uses '-' instead of '+', '_' instead of
	 * '/'
	 * 
	 * @param source
	 *            the string to decode (decoded in default encoding)
	 * @return the decoded data
	 */
	public static byte[] decodeWebSafe(byte[] source)
			throws Base64DecoderException {
		return decodeWebSafe(source, 0, source.length);
	}

	/**
	 * Decodes Base64 content in byte array format and returns the decoded byte
	 * array.
	 * 
	 * @param source
	 *            the Base64 encoded data
	 * @param off
	 *            the offset of where to begin decoding
	 * @param len
	 *            the length of characters to decode
	 * @return decoded data
	 * @since 1.3
	 * @throws Base64DecoderException
	 */
	public static byte[] decode(byte[] source, int off, int len)
			throws Base64DecoderException {
		return decode(source, off, len, DECODABET);
	}

	/**
	 * Decodes web safe Base64 content in byte array format and returns the
	 * decoded byte array. Web safe encoding uses '-' instead of '+', '_'
	 * instead of '/'
	 * 
	 * @param source
	 *            the Base64 encoded data
	 * @param off
	 *            the offset of where to begin decoding
	 * @param len
	 *            the length of characters to decode
	 * @return decoded data
	 */
	public static byte[] decodeWebSafe(byte[] source, int off, int len)
			throws Base64DecoderException {
		return decode(source, off, len, WEBSAFE_DECODABET);
	}

	/**
	 * Decodes Base64 content using the supplied decodabet and returns the
	 * decoded byte array.
	 * 
	 * @param source
	 *            the Base64 encoded data
	 * @param off
	 *            the offset of where to begin decoding
	 * @param len
	 *            the length of characters to decode
	 * @param decodabet
	 *            the decodabet for decoding Base64 content
	 * @return decoded data
	 */
	public static byte[] decode(byte[] source, int off, int len,
			byte[] decodabet) throws Base64DecoderException {
		int len34 = len * 3 / 4;
		byte[] outBuff = new byte[2 + len34]; // Upper limit on size of output
		int outBuffPosn = 0;

		byte[] b4 = new byte[4];
		int b4Posn = 0;
		int i = 0;
		byte sbiCrop = 0;
		byte sbiDecode = 0;
		for (i = 0; i < len; i++) {
			sbiCrop = (byte) (source[i + off] & 0x7f); // Only the low seven
														// bits
			sbiDecode = decodabet[sbiCrop];

			if (sbiDecode >= WHITE_SPACE_ENC) { // White space Equals sign or
												// better
				if (sbiDecode >= EQUALS_SIGN_ENC) {
					// An equals sign (for padding) must not occur at position 0
					// or 1
					// and must be the last byte[s] in the encoded value
					if (sbiCrop == EQUALS_SIGN) {
						int bytesLeft = len - i;
						byte lastByte = (byte) (source[len - 1 + off] & 0x7f);
						if (b4Posn == 0 || b4Posn == 1) {
							throw new Base64DecoderException(
									"invalid padding byte '=' at byte offset "
											+ i);
						} else if ((b4Posn == 3 && bytesLeft > 2)
								|| (b4Posn == 4 && bytesLeft > 1)) {
							throw new Base64DecoderException(
									"padding byte '=' falsely signals end of encoded value "
											+ "at offset " + i);
						} else if (lastByte != EQUALS_SIGN
								&& lastByte != NEW_LINE) {
							throw new Base64DecoderException(
									"encoded value has invalid trailing byte");
						}
						break;
					}

					b4[b4Posn++] = sbiCrop;
					if (b4Posn == 4) {
						outBuffPosn += decode4to3(b4, 0, outBuff, outBuffPosn,
								decodabet);
						b4Posn = 0;
					}
				}
			} else {
				throw new Base64DecoderException(
						"Bad Base64 input character at " + i + ": "
								+ source[i + off] + "(decimal)");
			}
		}

		// Because web safe encoding allows non padding base64 encodes, we
		// need to pad the rest of the b4 buffer with equal signs when
		// b4Posn != 0. There can be at most 2 equal signs at the end of
		// four characters, so the b4 buffer must have two or three
		// characters. This also catches the case where the input is
		// padded with EQUALS_SIGN
		if (b4Posn != 0) {
			if (b4Posn == 1) {
				throw new Base64DecoderException(
						"single trailing character at offset " + (len - 1));
			}
			b4[b4Posn++] = EQUALS_SIGN;
			outBuffPosn += decode4to3(b4, 0, outBuff, outBuffPosn, decodabet);
		}

		byte[] out = new byte[outBuffPosn];
		System.arraycopy(outBuff, 0, out, 0, outBuffPosn);
		return out;
	}
}
