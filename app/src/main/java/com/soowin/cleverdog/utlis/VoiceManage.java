package com.soowin.cleverdog.utlis;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.soowin.cleverdog.activity.index.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hxt on 2017/8/29.
 * 语音队列管理
 */

public class VoiceManage {
    public static final String TAG = VoiceManage.class.getSimpleName();

    private List<String> voiceList100;
    private List<String> voiceList20;

    private List<String> voiceList;

    private SpeechSynthesizer mTts;
    private boolean isOkSpeak = false;

    public VoiceManage(Context context) {
        initXunFei(context);
        getVoiceManage();
    }

    private List<String> getVoiceManage() {
        if (voiceList100 == null)
            voiceList100 = new ArrayList<>();
        if (voiceList20 == null)
            voiceList20 = new ArrayList<>();
        if (voiceList == null)
            voiceList = new ArrayList<>();
        return voiceList100;
    }

    /**
     * 添加一条100m语音
     */
    public void addVoice100(String str) {
        if (voiceList100 == null)
            getVoiceManage();
        voiceList100.add(str);
        setVoiceData();
    }

    /**
     * 设置播报列表
     */
    private void setVoiceData() {
        voiceList.clear();
        voiceList.addAll(voiceList20);
        voiceList.addAll(voiceList100);
        PublicApplication.mAdapter.setData(voiceList);
    }

    /**
     * 添加一条20m语音
     */
    public void addVoice20(String str) {
        if (voiceList20 == null)
            getVoiceManage();
        if (voiceList20.size() < 1) {
            voiceList20.add(str);
            setVoiceData();
        } else if (str.equals(voiceList20.get(voiceList20.size() - 1)) && str.equals("灵狗持续为您服务！")) {

        } else {
            voiceList20.add(str);
            setVoiceData();
        }
    }

    /**
     * 播放一条语音
     */
    public boolean playVoice() {
        if (mTts != null) {
            if (!mTts.isSpeaking()) {
                if (voiceList20 != null && voiceList20.size() > 0) {
                    if (voiceList20.get(0) != null) {
                        mTts.startSpeaking(voiceList20.get(0), mSynListener20);
                        return true;
                    }
                } else if (voiceList100 != null && voiceList100.size() > 0) {
                    if (voiceList100.get(0) != null) {
                        mTts.startSpeaking(voiceList100.get(0), mSynListener100);
                        return true;
                    }
                }
                return false;
            } else return true;
        } else {
            initXunFei(PublicApplication.getContext());
            return true;
        }
    }

    /**
     * 播放完成一条100m语音
     */
    public void delVoice100() {
        if (voiceList100 != null)
            if (voiceList100.size() > 0)
                if (voiceList100.get(0) != null) {
                    voiceList100.remove(0);
                    setVoiceData();
                }
    }

    /**
     * 播放完成一条20m语音
     */
    public void delVoice20() {
        if (voiceList20 != null)
            if (voiceList20.size() > 0)
                if (voiceList20.get(0) != null) {
                    voiceList20.remove(0);
                    setVoiceData();
                }
    }

    /**
     * 退出登录删除所有点
     */
    public void delVoiceAll() {
        if (voiceList20 != null)
            voiceList20.clear();
        if (voiceList100 != null)
            voiceList100.clear();
        if (voiceList != null)
            voiceList.clear();
        setVoiceData();
    }

    //*****************************************************讯飞部分
    private void initXunFei(Context context) {
        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        mTts = SpeechSynthesizer.createSynthesizer(context, mTtsInitListener);
        //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        if (mTts != null) {
            mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
            mTts.setParameter(SpeechConstant.SPEED, "80");//设置语速
            mTts.setParameter(SpeechConstant.VOLUME, "100");//设置音量，范围0~100
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        }
    }

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                Log.e(TAG, "mTtsInitListener: 初始化失败,错误码：" + code);
                isOkSpeak = false;
            } else {
                isOkSpeak = true;
            }
        }
    };
    //合成监听器
    private SynthesizerListener mSynListener100 = new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
            PublicApplication.mVoiceManage.delVoice100();
            PublicApplication.mVoiceManage.playVoice();
        }

        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }

        //开始播放
        public void onSpeakBegin() {

        }

        //暂停播放
        public void onSpeakPaused() {

        }

        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        //恢复播放回调接口
        public void onSpeakResumed() {
        }

        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }
    };
    //合成监听器
    private SynthesizerListener mSynListener20 = new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
            PublicApplication.mVoiceManage.delVoice20();
            PublicApplication.mVoiceManage.playVoice();
        }

        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }

        //开始播放
        public void onSpeakBegin() {
        }

        //暂停播放
        public void onSpeakPaused() {
        }

        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        //恢复播放回调接口
        public void onSpeakResumed() {
        }

        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }
    };
    //*****************************************************讯飞部分
}
