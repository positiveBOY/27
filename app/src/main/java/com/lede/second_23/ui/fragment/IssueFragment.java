//package com.lede.second_23.ui.fragment;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.bumptech.glide.Glide;
//import com.google.gson.Gson;
//import com.lede.second_23.R;
//import com.lede.second_23.bean.UploadTextBean;
//import com.lede.second_23.global.GlobalConstants;
//import com.lede.second_23.ui.activity.MainActivity;
//import com.lede.second_23.utils.L;
//import com.lede.second_23.utils.ProgressDialogUtils;
//import com.lede.second_23.utils.SPUtils;
//import com.lede.second_23.utils.VideoUtils;
//import com.luck.picture.lib.model.FunctionConfig;
//import com.luck.picture.lib.model.FunctionOptions;
//import com.luck.picture.lib.model.PictureConfig;
//import com.yalantis.ucrop.entity.LocalMedia;
//import com.yolanda.nohttp.NoHttp;
//import com.yolanda.nohttp.RequestMethod;
//import com.yolanda.nohttp.rest.OnResponseListener;
//import com.yolanda.nohttp.rest.Request;
//import com.yolanda.nohttp.rest.RequestQueue;
//import com.yolanda.nohttp.rest.Response;
//import com.zhy.adapter.recyclerview.CommonAdapter;
//import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
//import com.zhy.adapter.recyclerview.base.ViewHolder;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by ld on 17/5/22.
// */
//
//public class IssueFragment extends Fragment implements View.OnClickListener, OnResponseListener<String> {
//    private Context mContext;
//    private RequestQueue requestQueue;
//    private List<LocalMedia> selectMedia = new ArrayList<>();
//    private Gson mGson = new Gson();
//    private CommonAdapter mAdapter;
//    private int imgOrVideoType = 3;//3表示未选择图片或者视频 0图片 1视频
//
//    private static final int UPLOADVIDEO_REQUEST = 3000;
//    private static final int UPLOADIMG_REQUEST = 2000;
//    private static final int UPLOADTEXT_REQUEST = 1000;
//
//    private GridLayoutManager gridLayoutManager;
//    private Dialog loadingDialog2;
//    private View fragmentView;
//    private ImageView iv_back;
//    private EditText et_text;
//    private RecyclerView rv_show;
//    private ImageView iv_send;
//    private int num=0;//计数
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        mContext=context;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        fragmentView=inflater.inflate(R.layout.activity_issue,container,false);
//        //获取服务器队列
//        requestQueue = GlobalConstants.getRequestQueue();
//        initView();
//        initRecyclerView();
//        return fragmentView;
//
//    }
//
//    private void initView() {
//        iv_back = (ImageView) fragmentView.findViewById(R.id.iv_issue_activity_back);
//        iv_back.setOnClickListener(this);
//        et_text = (EditText) fragmentView.findViewById(R.id.et_issue_activity_text);
//        rv_show = (RecyclerView) fragmentView.findViewById(R.id.rv_issue_activity_show);
//        iv_send = (ImageView) fragmentView.findViewById(R.id.iv_issue_activity_send);
//        iv_send.setOnClickListener(this);
//    }
//
//    private void initRecyclerView() {
//        mAdapter = new CommonAdapter<LocalMedia>(mContext, R.layout.item, selectMedia) {
//            @Override
//            protected void convert(ViewHolder holder, LocalMedia localMedia, final int position) {
//                ImageView iv_show = holder.getView(R.id.iv_item);
//                ImageView iv_delete=holder.getView(R.id.iv_delete);
////                Log.i("TAG", "convert: path"+localMedia.getPath()+" "+localMedia.getCompressPath()+" "+localMedia.getCutPath());
//                if (position == selectMedia.size() - 1) {
//                    Glide.with(mContext)
//                            .load(R.mipmap.add)
//                            .into(iv_show);
//                } else {
//                    Glide.with(mContext)
//                            .load(new File(localMedia.getPath()))
//                            .into(iv_show);
//                }
//                iv_delete.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        selectMedia.remove(position);
//                        mAdapter.notifyDataSetChanged();
//                        if (selectMedia.size() == 1 && selectMedia.get(0) == null) {
//                            imgOrVideoType = 3;
//                        }
//                    }
//                });
//            }
//        };
//        gridLayoutManager = new GridLayoutManager(mContext, 3, LinearLayoutManager.VERTICAL, false);
////        new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
//        rv_show.setLayoutManager(gridLayoutManager);
//        rv_show.setAdapter(mAdapter);
//        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
//                if (position == selectMedia.size() - 1) {
//                    if (imgOrVideoType == 3) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                        builder.setIcon(R.mipmap.add);
//                        builder.setTitle("选择操作");
//                        //    指定下拉列表的显示数据
//                        final String[] cities = {"图片", "视频"};
//                        //    设置一个下拉的列表选择项
//                        builder.setItems(cities, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                chooseImgOrVideo(which);
//                            }
//                        });
//                        builder.show();
//                    } else {
//                        chooseImgOrVideo(imgOrVideoType);
//                    }
//                }
//            }
//
//            @Override
//            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
//                return false;
//            }
//        });
//    }
//
//    private void chooseImgOrVideo(int type) {
//        switch (type) {
//            case 0:
//                imgOrVideoType = 0;
//                FunctionOptions options1 = new FunctionOptions.Builder()
//                        .setType(FunctionConfig.TYPE_IMAGE) // 图片or视频 FunctionConfig.TYPE_IMAGE  TYPE_VIDEO
//                        .setCropMode(FunctionConfig.CROP_MODEL_1_1) // 裁剪模式 默认、1:1、3:4、3:2、16:9
//                        .setCompress(true) //是否压缩
//                        .setEnablePixelCompress(true) //是否启用像素压缩
//                        .setEnableQualityCompress(true) //是否启质量压缩
//                        .setMaxSelectNum(9) // 可选择图片的数量
//                        .setMinSelectNum(1)// 图片或视频最低选择数量，默认代表无限制
//                        .setSelectMode(FunctionConfig.MODE_MULTIPLE) // 单选 or 多选 FunctionConfig.MODE_SINGLE FunctionConfig.MODE_MULTIPLE
//                        .setVideoS(0)// 查询多少秒内的视频 单位:秒
//                        .setShowCamera(true) //是否显示拍照选项 这里自动根据type 启动拍照或录视频
//                        .setEnablePreview(true) // 是否打开预览选项
//                        .setEnableCrop(false) // 是否打开剪切选项
//                        .setCircularCut(false)// 是否采用圆形裁剪
//                        .setPreviewVideo(true) // 是否预览视频(播放) mode or 多选有效
////                        .setCheckedBoxDrawable() // 选择图片样式
////                        .setRecordVideoDefinition() // 视频清晰度
////                        .setRecordVideoSecond() // 视频秒数
////                        .setCustomQQ_theme()// 可自定义QQ数字风格，不传就默认是蓝色风格
//                        .setGif(false)// 是否显示gif图片，默认不显示
////                        .setCropW() // cropW-->裁剪宽度 值不能小于100  如果值大于图片原始宽高 将返回原图大小
////                        .setCropH() // cropH-->裁剪高度 值不能小于100 如果值大于图片原始宽高 将返回原图大小
////                        .setMaxB() // 压缩最大值 例如:200kb  就设置202400，202400 / 1024 = 200kb左右
////                        .setPreviewColor(Color.parseColor("")) //预览字体颜色
////                        .setCompleteColor() //已完成字体颜色
////                        .setPreviewTopBgColor()//预览图片标题背景色
////                        .setPreviewBottomBgColor() //预览底部背景色
////                        .setBottomBgColor() //图片列表底部背景色
////                        .setGrade() // 压缩档次 默认三档
//                        .setCheckNumMode(true) //QQ选择风格
////                        .setCompressQuality() // 图片裁剪质量,默认无损
//                        .setImageSpanCount(3) // 每行个数
//                        .setSelectMedia(selectMedia) // 已选图片，传入在次进去可选中，不能传入网络图片
////                        .setCompressFlag(1) // 1 系统自带压缩 2 luban压缩
////                        .setCompressW() // 压缩宽 如果值大于图片原始宽高无效
////                        .setCompressH() // 压缩高 如果值大于图片原始宽高无效
////                        .setThemeStyle() // 设置主题样式
////                        .setPicture_title_color() // 设置标题字体颜色
////                        .setPicture_right_color() // 设置标题右边字体颜色
////                        .setLeftBackDrawable() // 设置返回键图标
////                        .setStatusBar() // 设置状态栏颜色，默认是和标题栏一致
//                        .setImmersive(false)// 是否改变状态栏字体颜色(黑色)
//                        .setNumComplete(false) // 0/9 完成  样式
//                        .setClickVideo(true)// 点击声音
//                        .create();
//                selectMedia.remove(selectMedia.get(selectMedia.size() - 1));
//                PictureConfig.getInstance().init(options1).openPhoto((Activity) mContext, resultCallback);
////                PictureConfig.getInstance().init(options1).startOpenCamera();
//                selectMedia.add(null);
//                break;
//            case 1:
//                imgOrVideoType = 1;
//                FunctionOptions options = new FunctionOptions.Builder()
//                        .setType(FunctionConfig.TYPE_VIDEO)
//                        .setCompress(true) //是否压缩
//                        .setEnablePixelCompress(true) //是否启用像素压缩
//                        .setEnableQualityCompress(true) //是否启质量压缩
//                        .setMaxSelectNum(1) // 可选择图片的数量
//                        .setMinSelectNum(1)// 图片或视频最低选择数量，默认代表无限制
//                        .setSelectMode(FunctionConfig.MODE_MULTIPLE) // 单选 or 多选 FunctionConfig.MODE_SINGLE FunctionConfig.MODE_MULTIPLE
//                        .setVideoS(15)// 查询多少秒内的视频 单位:秒
//                        .setShowCamera(true) //是否显示拍照选项 这里自动根据type 启动拍照或录视频
//                        .setEnablePreview(true) // 是否打开预览选项
//                        .setEnableCrop(false) // 是否打开剪切选项
//                        .setCircularCut(false)// 是否采用圆形裁剪
//                        .setPreviewVideo(true) // 是否预览视频(播放) mode or 多选有效
//                        .setCheckNumMode(true) //QQ选择风格
////                        .setCompressQuality() // 图片裁剪质量,默认无损
//                        .setImageSpanCount(3) // 每行个数
//                        .setSelectMedia(selectMedia) // 已选图片，传入在次进去可选中，不能传入网络图片
//                        .setImmersive(false)// 是否改变状态栏字体颜色(黑色)
//                        .setNumComplete(false) // 0/9 完成  样式
//                        .setClickVideo(true)// 点击声音
//                        .create();
//                selectMedia.remove(selectMedia.get(selectMedia.size() - 1));
//                PictureConfig.getInstance().init(options).openPhoto((Activity) mContext, resultCallback);
//                selectMedia.add(null);
//                break;
//        }
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.iv_issue_activity_back:
//                MainActivity.instance.vp_main_fg.setCurrentItem(1);
//                break;
//            case R.id.iv_issue_activity_send:
//                if (TextUtils.isEmpty(et_text.getText().toString().trim()) || (selectMedia.size() == 1 && selectMedia.get(0) == null)) {
//
//                    Toast.makeText(mContext, "请输入文字或者选择图片视频", Toast.LENGTH_SHORT).show();
//                    break;
//                } else {
//                    uploadTextService();
//                }
//                try {
//                    Log.i("TAB", "click:" + et_text.getText() + "123");
//                } catch (Exception e) {
//                    Log.i("TAB", "click: error" + e.getMessage());
//                }
//
//                break;
//
//        }
//    }
//
//    private PictureConfig.OnSelectResultCallback resultCallback = new PictureConfig.OnSelectResultCallback() {
//        @Override
//        public void onSelectSuccess(List<LocalMedia> resultList) {
//            // 多选回调
//            selectMedia.clear();
//            selectMedia.addAll(resultList);
//            selectMedia.add(null);
////            Log.i("TAG", "onSelectSuccess: "+selectMedia.size());
//            Log.i("callBack_result", selectMedia.size() + "");
//            LocalMedia media = resultList.get(0);
//            if (media.isCut() && !media.isCompressed()) {
//                // 裁剪过
//                String path = media.getCutPath();
//            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
//                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
//                String path = media.getCompressPath();
//            } else {
//                // 原图地址
//                String path = media.getPath();
//            }
//            if (selectMedia != null) {
////                madapter.setList(selectMedia);
//                Log.i("TAG", "onSelectSuccess: selectMedia != null");
//                mAdapter.notifyDataSetChanged();
//            }
//        }
//
//        @Override
//        public void onSelectSuccess(LocalMedia media) {
//            // 单选回调
//            selectMedia.clear();
//            selectMedia.add(media);
//            selectMedia.add(null);
//            if (selectMedia != null) {
////                adapter.setList(selectMedia);
//                mAdapter.notifyDataSetChanged();
//            }
//        }
//    };
//
//    /**
//     * 上传文字请求
//     */
//    private void uploadTextService() {
//        loadingDialog2 = ProgressDialogUtils.createLoadingDialog(mContext, "正在上传请稍等...");
//        loadingDialog2.show();
//        Request<String> uploadTextRequest = NoHttp.createStringRequest(GlobalConstants.URL + "/forums/update", RequestMethod.POST);
//        uploadTextRequest.add("access_token", (String) SPUtils.get(mContext, GlobalConstants.TOKEN, ""));
//        uploadTextRequest.add("text", et_text.getText().toString().trim());
//        requestQueue.add(UPLOADTEXT_REQUEST, uploadTextRequest,this);
//
//    }
//
//    /**
//     * 上传图片
//     */
//    public void uploadImgServce(int forumId) {
////        loadingDialog2 = ProgressDialogUtils.createLoadingDialog(mContext, "正在上传请稍等...");
////        loadingDialog2.show();
//        for (int i = selectMedia.size() - 1; i >=0 ; i--) {
//            Request<String> uploadRequest = NoHttp.createStringRequest(GlobalConstants.URL + "/forums/upload", RequestMethod.POST);
//            uploadRequest.add("access_token", (String) SPUtils.get(mContext, GlobalConstants.TOKEN, ""));
//            uploadRequest.add("pic", new File(selectMedia.get(i).getPath()));
//            uploadRequest.add("forumId", forumId);
//            requestQueue.add(UPLOADIMG_REQUEST, uploadRequest, this);
//        }
//
//    }
//
//    /**
//     * 上传视频请求
//     *
//     * @param forumId
//     */
//    public void uploadVideoServce(int forumId) {
//
////        loadingDialog2.show();
//        Request<String> uploadVideoRequest = NoHttp.createStringRequest(GlobalConstants.URL + "/forums/upload_media", RequestMethod.POST);
//        uploadVideoRequest.add("access_token", (String) SPUtils.get(mContext, GlobalConstants.TOKEN, ""));
//        uploadVideoRequest.add("pic", new File(VideoUtils.bitmap2File(VideoUtils.getVideoThumb(selectMedia.get(0).getPath()), "cacher")));
//        uploadVideoRequest.add("media", new File(selectMedia.get(0).getPath()));
//        uploadVideoRequest.add("forumId", forumId);
////        uploadRequest.add("labels","");
//        requestQueue.add(UPLOADVIDEO_REQUEST, uploadVideoRequest, this);
////
//    }
//
//
////    @Override
////    protected void onResume() {
////        super.onResume();
////        Log.i("TAB", "onResume: ");
//////        if (selectMedia.size()==0) {
//////            imgOrVideoType=3;
//////            selectMedia.add(null);
//////            mAdapter.notifyDataSetChanged();
//////        }
////        if (selectMedia.size() == 1 && selectMedia.get(0) == null) {
////            imgOrVideoType = 3;
////        }
////    }
//
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.i("TAB", "onResume: ");
////        if (selectMedia.size()==0) {
////            imgOrVideoType=3;
////            selectMedia.add(null);
////            mAdapter.notifyDataSetChanged();
////        }
//        if (selectMedia.size() == 1 && selectMedia.get(0) == null) {
//            imgOrVideoType = 3;
//        }
//    }
//
//    @Override
//    public void onStart(int what) {
//
//    }
//
//    @Override
//    public void onSucceed(int what, Response<String> response) {
//        switch (what) {
//            case UPLOADTEXT_REQUEST:
//                L.i(response.get());
//                parseUpTextJson(response.get());
//                break;
//            case UPLOADIMG_REQUEST:
//                L.i(response.get());
//                num++;
//                if (num==selectMedia.size()) {
//                    if (loadingDialog2!=null) {
//                        loadingDialog2.dismiss();
//                        Toast.makeText(mContext, "图片上传成功", Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//
//                break;
//            case UPLOADVIDEO_REQUEST:
//                num++;
//                if (num==selectMedia.size()) {
//                    if (loadingDialog2!=null) {
//                        loadingDialog2.dismiss();
//                        Toast.makeText(mContext, "视频上传成功", Toast.LENGTH_SHORT).show();
//                    }
//                    SPUtils.put(mContext,GlobalConstants.IS_ISSUE,true);
////                getActivity()
//                    MainActivity.instance.vp_main_fg.setCurrentItem(1);
//                }
//                L.i(response.get());
//                break;
//        }
//    }
//
//    @Override
//    public void onFailed(int what, Response<String> response) {
//
//    }
//
//    @Override
//    public void onFinish(int what) {
//
//    }
//
//    private void parseUpTextJson(String json) {
//        UploadTextBean uploadTextBean = mGson.fromJson(json, UploadTextBean.class);
//        if (imgOrVideoType == 0) {
//            uploadImgServce(uploadTextBean.getData().getForumId());
//        }else {
//            uploadVideoServce(uploadTextBean.getData().getForumId());
//        }
//
////        uploadVideoServce(uploadTextBean.getData().getForumId());
//    }
//}
