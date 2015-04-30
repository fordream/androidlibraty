package com.vnp.core.service.callapi;

import android.os.Bundle;
import com.vnp.core.service.callapi.ICallApiParacelable;
interface ICallApiService {


	
            
            /**
            *method is get, post, put
            */
   void callApi(in ICallApiParacelable paracel);
   void callApiThread(in ICallApiParacelable paracel);

}