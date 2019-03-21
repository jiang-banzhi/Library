package com.banzhi.lib.utils;

import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * <pre>
 * author : No.1
 * time : 2017/7/11.
 * desc :
 * </pre>
 */

public class GsonUtils {
    private static GsonUtils mINSTANCE;
    Gson mGson;
    Class clz;//数据基类

    /**
     *
     * @param clz  basebean 实体类的基类
     * @return
     */
    public static GsonUtils getInstance(Class clz) {
        if (mINSTANCE == null) {
            synchronized (GsonUtils.class) {
                if (mINSTANCE == null) {
                    mINSTANCE = new GsonUtils(clz);
                }
            }
        }
        return mINSTANCE;
    }

    private GsonUtils(Class clz) {
        this.clz = clz;
        mGson = new Gson();
    }

    public <T> String toJson(T t) {
        return mGson.toJson(t);
    }

    public <T> T fromJson(String json, Type type) {
        return mGson.fromJson(json, type);
    }

    public <T> T fromJson(String json, Class clz) {
        T t = null;
        try {
            //{"Code":0,"Message":"success",Data:{}}
            t = fromJsonObject(json, clz);
        } catch (Exception e) {
            e.printStackTrace();
            //{"Code":0,"Message":"success",Data:[]}
            t = fromJsonArray(json, clz);
        }
        return t;
    }

    public <T> T fromJsonObject(String json, Class clazz) {
        Type type = new ParameterizedTypeImpl(clz, new Type[]{clazz});
        Log.i("result", "fromJsonObject: "+json);
        return mGson.fromJson(json, type);
    }

    public <T> T fromJsonArray(String json, Class clazz) {
        // 生成List<T> 中的 List<T>
        Type listType = new ParameterizedTypeImpl(List.class, new Class[]{clazz});
        // 根据List<T>生成完整的Result<List<T>>
        Type type = new ParameterizedTypeImpl(clz, new Type[]{listType});
        Log.i("result", "fromJsonArray: "+json);
        return mGson.fromJson(json, type);
    }


    public class ParameterizedTypeImpl implements ParameterizedType {
        private final Class raw;
        private final Type[] args;

        public ParameterizedTypeImpl(Class raw, Type[] args) {
            this.raw = raw;
            this.args = args != null ? args : new Type[0];
        }

        @Override
        public Type[] getActualTypeArguments() {
            return args;
        }

        @Override
        public Type getRawType() {
            return raw;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
