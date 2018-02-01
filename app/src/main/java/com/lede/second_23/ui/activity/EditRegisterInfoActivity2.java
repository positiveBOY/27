package com.lede.second_23.ui.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.gson.Gson;
import com.lede.second_23.MyApplication;
import com.lede.second_23.R;
import com.lede.second_23.bean.PersonalAlbumBean;
import com.lede.second_23.global.GlobalConstants;
import com.lede.second_23.interface_utils.MyCallBack;
import com.lede.second_23.interface_utils.OnUploadFinish;
import com.lede.second_23.service.AlbumService;
import com.lede.second_23.service.PickService;
import com.lede.second_23.service.UploadAlbumService;
import com.lede.second_23.service.UserInfoService;
import com.lede.second_23.ui.base.BaseActivity;
import com.lede.second_23.utils.SPUtils;
import com.lede.second_23.utils.SnackBarUtil;
import com.lede.second_23.utils.UiUtils;
import com.lljjcoder.citypickerview.widget.CityPicker;
import com.luck.picture.lib.model.PictureConfig;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yalantis.ucrop.entity.LocalMedia;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lede.second_23.global.GlobalConstants.USERID;

/**
 * Created by ld on 18/1/4.
 */

public class EditRegisterInfoActivity2 extends BaseActivity {


    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;


    @Bind(R.id.user_name)
    TextView userName;
    @Bind(R.id.user_info)
    TextView userInfo;
    @Bind(R.id.user_sex)
    TextView userSex;
    @Bind(R.id.user_age)
    TextView userAge;
    @Bind(R.id.user_city)
    TextView userCity;
    @Bind(R.id.user_hobby)
    TextView userHobby;
    @Bind(R.id.user_school)
    TextView userSchool;



    private boolean hasChangedUserName=false;
    private String originUserName;
    private String currentUserName;
    private String currentUserInfo;
    private String currentUserSex;
    private String currentUserAge;
    private String currentUserCity;
    private String currentUserHobby;
    private String currentUserSchool;



    private Gson mGson;

    private Activity context;
    private MultiItemTypeAdapter mAdapter;
    private ArrayList<PersonalAlbumBean.DataBean.SimpleBean.UserPhotoBean> mAlbumList = new ArrayList<>();
    private int mAlbumSize=0;
    private ArrayList<Object> imageList=new ArrayList<>();
    private PersonalAlbumBean.DataBean.UserInfo userInfoBean;
    private int itemWidth;
    private RequestManager requestManager;

    private HeadItem headItem=new HeadItem();
    private String userID;
    private AlbumService albumService;
    private Snackbar snackbar;
    private PictureConfig.OnSelectResultCallback portraitPickCallback;
    private String selectedImg;

    private UserInfoService userInfoService;

    //该页面要上传的三部分内容 要确保都ok了 再跳转下一页面
    private boolean isPrortraitOK = false;
    private boolean isPhotoOK = false;
    private boolean isUserInfoOK = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        context = this;
        ButterKnife.bind(context);
        requestManager = Glide.with(context);


        itemWidth = (UiUtils.getScreenWidth() / 3);

        userID = (String) SPUtils.get(context, USERID, "");


        initView();

        initEvent();
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableRefresh(false);


    }

    @OnClick({R.id.back,R.id.save, R.id.user_name_layout, R.id.user_info_layout, R.id.user_sex_layout,

            R.id.user_age_layout, R.id.user_city_layout, R.id.user_hobby_layout,R.id.user_school_layout
    })
    public void onClick(View view) {
        Intent intent=null;
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.save:
                saveUserInfo();
                break;
            case R.id.user_name_layout:
                intent = new Intent(this, NicknameOrHobbyOrSignActivity.class);
                intent.putExtra("type", 0);
                intent.putExtra("body", userName.getText().toString().trim());
                startActivityForResult(intent, 0);
                break;
            case R.id.user_info_layout:
                intent = new Intent(this, NicknameOrHobbyOrSignActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("body", userInfo.getText().toString().trim());
                startActivityForResult(intent, 1);
                break;
            case R.id.user_sex_layout:
                intent = new Intent(this, SexDialogActivity.class);
                intent.putExtra("sex", userSex.getText().toString().equals("男"));
                startActivityForResult(intent, 3);
                break;

            case R.id.user_age_layout:
                showAgeDialog();
                break;
            case R.id.user_city_layout:
                showCityDialog();
                break;
            case R.id.user_hobby_layout:
                intent = new Intent(this, NicknameOrHobbyOrSignActivity.class);
                intent.putExtra("type", 2);
                intent.putExtra("body", userHobby.getText().toString().trim());
                startActivityForResult(intent, 2);
                break;
            case R.id.user_school_layout:
                intent = new Intent(this, NicknameOrHobbyOrSignActivity.class);
                intent.putExtra("type", 4);
                intent.putExtra("body", userSchool.getText().toString().trim());
                startActivityForResult(intent, 4);
                break;
            default:
                break;
        }
    }

    private void initView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));



        mAdapter=new MultiItemTypeAdapter(this,imageList);
        mAdapter.addItemViewDelegate(new HeadImgItemDelegate());
        mAdapter.addItemViewDelegate(new UserphotoItemDelegate());
        mAdapter.addItemViewDelegate(new EmptyItemDelegate());


        mRecyclerView.setAdapter(mAdapter);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {

                doRequest();
            }
        });


        userInfoService=new UserInfoService(this);
        portraitPickCallback = new PictureConfig.OnSelectResultCallback() {
            @Override
            public void onSelectSuccess(List<LocalMedia> resultList) {
                // 多选回调

            }

            @Override
            public void onSelectSuccess(LocalMedia media) {
                // 单选回调
                selectedImg = media.getCutPath();
                headItem.setSelectedImg(selectedImg);
                isPrortraitOK=true;

                mAdapter.notifyDataSetChanged();

            }

        };
    }


    public class HeadItem{

        private String selectedImg;

        public String getSelectedImg() {
            return selectedImg;
        }

        public void setSelectedImg(String selectedImg) {
            this.selectedImg = selectedImg;
        }
    }


    public class HeadImgItemDelegate implements ItemViewDelegate<Object> {
        @Override
        public int getItemViewLayoutId() {
            return R.layout.edit_info_head_img;
        }

        @Override
        public boolean isForViewType(Object item, int position) {
            return item instanceof HeadItem;
        }

        @Override
        public void convert(ViewHolder holder, Object o, int position) {
            holder.getConvertView().setLayoutParams(new LinearLayout.LayoutParams(itemWidth,itemWidth));
            HeadItem headItem= (HeadItem) o;
            ImageView imageView=holder.getView(R.id.image);
            Glide.with(context).load(headItem.getSelectedImg()).into(imageView);

            holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //点击更换头像
                    PickService pickService = new PickService(EditRegisterInfoActivity2.this);
                    pickService.pickPortrait(portraitPickCallback);


                }
            });


        }
    }

    public class UserphotoItemDelegate implements ItemViewDelegate<Object>{
        @Override
        public int getItemViewLayoutId() {
            return R.layout.edit_info_album_item;
        }

        @Override
        public boolean isForViewType(Object item, int position) {
            return item instanceof PersonalAlbumBean.DataBean.SimpleBean.UserPhotoBean;
        }

        @Override
        public void convert(ViewHolder holder, Object o, final int position) {
            holder.getConvertView().setLayoutParams(new LinearLayout.LayoutParams(itemWidth,itemWidth));
            final PersonalAlbumBean.DataBean.SimpleBean.UserPhotoBean userPhotoBean= (PersonalAlbumBean.DataBean.SimpleBean.UserPhotoBean) o;
            ImageView image=holder.getView(R.id.image);
            ImageView delete=holder.getView(R.id.delete_image);
            Glide.with(context).load(userPhotoBean.getUrlImg()).into(image);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteAlbum(Integer.parseInt(userPhotoBean.getId()));

                }
            });

        }
    }
    public class EmptyItem {

    }
    //凑齐6个item，不够的用这个item凑
    public class EmptyItemDelegate implements  ItemViewDelegate<Object>{

        @Override
        public int getItemViewLayoutId() {
            return R.layout.edit_info_empty_item;
        }

        @Override
        public boolean isForViewType(Object item, int position) {
            return item instanceof EmptyItem;
        }

        @Override
        public void convert(ViewHolder holder, Object o, int position) {
            holder.getConvertView().setLayoutParams(new LinearLayout.LayoutParams(itemWidth,itemWidth));

            holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //点击上传相册
                    upLoadAlbum();
                }
            });

        }
    }


    private void initEvent() {

        albumService = new AlbumService(context);
        userInfoService = new UserInfoService(context);

        doRequest();


    }




    private void doRequest() {

        albumService.getMyAlbum(userID, new MyCallBack() {
            @Override
            public void onSuccess(Object o) {
                refreshLayout.finishRefresh();
                PersonalAlbumBean personalAlbumBean = (PersonalAlbumBean) o;
                List<PersonalAlbumBean.DataBean.SimpleBean.UserPhotoBean> list = personalAlbumBean.getData().getSimple().getList();


                imageList.clear();
                imageList.add(headItem);
                imageList.addAll(list);
                mAlbumSize=list.size();
                if(mAlbumSize!=0){
                    isPhotoOK=true;
                }else{
                    isPhotoOK=false;
                }
                for (int i=0;i<(5-mAlbumSize);i++){
                    imageList.add(new EmptyItem());
                }
//                mAlbumList.clear();
//
//
//
//                mAlbumList.addAll(list);
//



                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFail(String mistakeInfo) {

            }
        });


    }



    //将从网络刷新出的数据保存下来
    private void setCurrentUserInfo() {

        currentUserName=userInfoBean.getNickName();
        originUserName=currentUserName;//也就是服务器上的用户名，每次提交之前 需要比对提交的用户名是否变化
        currentUserInfo=userInfoBean.getNote();
        currentUserSex=userInfoBean.getSex();
        currentUserAge=userInfoBean.getHobby();
        currentUserCity=userInfoBean.getAddress();
        currentUserHobby=userInfoBean.getWechat();
        currentUserSchool=userInfoBean.getHometown();

    }

    private void setUserInfo() {

        userName.setText(currentUserName);
        userInfo.setText(currentUserInfo);
        userSex.setText(currentUserSex);
        userAge.setText(currentUserAge);
        userCity.setText(currentUserCity);
        userHobby.setText(currentUserHobby);
        userSchool.setText(currentUserSchool);
    }

    private void saveUserInfo(){

        if (userName.getText().toString().trim().equals("") || userName.getText().toString().trim().equals("昵称")) {
            Toast.makeText(context, "请输入昵称", Toast.LENGTH_SHORT).show();
            return;
        } else if (userInfo.getText().toString().trim().equals("") || userInfo.getText().toString().trim().equals("签名")) {
            Toast.makeText(context, "请输入签名", Toast.LENGTH_SHORT).show();
            return;
        } else if (userCity.getText().toString().trim().equals("") || userCity.getText().toString().trim().equals("城市")) {
            Toast.makeText(context, "请输入城市", Toast.LENGTH_SHORT).show();
            return;
        } else if (userHobby.getText().toString().trim().equals("") || userHobby.getText().toString().trim().equals("爱好")) {
            Toast.makeText(context, "请输入爱好", Toast.LENGTH_SHORT).show();
            return;
        } else if (userAge.getText().toString().trim().equals("") || userAge.getText().toString().trim().equals("年龄")) {
            Toast.makeText(context, "请选择年龄", Toast.LENGTH_SHORT).show();
            return;
        } else if (userSex.getText().toString().trim().equals("") || userSex.getText().toString().trim().equals("性别")) {
            Toast.makeText(context, "请选择性别", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isPrortraitOK) {
            Toast.makeText(context, "请上传头像", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isPhotoOK) {
            Toast.makeText(context, "请至少上传一张图片", Toast.LENGTH_SHORT).show();
            return;
        }



        snackbar= SnackBarUtil.getLongTimeInstance(mRecyclerView,"信息保存中，请稍候...");
        snackbar.show();
        userInfoService.createUserInfo(selectedImg,currentUserName, currentUserInfo, currentUserSex, currentUserAge, currentUserCity, currentUserHobby, currentUserSchool,
                new MyCallBack() {
                    @Override
                    public void onSuccess(Object o) {


                        snackbar.dismiss();
                        Toast.makeText(EditRegisterInfoActivity2.this, "保存信息成功", Toast.LENGTH_SHORT).show();
                        SPUtils.put(EditRegisterInfoActivity2.this, GlobalConstants.IS_EDITINFO, true);

                        Intent intent = new Intent(EditRegisterInfoActivity2.this, WelcomeActivity.class);
                        startActivity(intent);
                        MyApplication.instance.getRongIMTokenService();

                    }

                    @Override
                    public void onFail(String mistakeInfo) {
                        snackbar.dismiss();
                        if(mistakeInfo.equals("用户没有登录")){
                            Toast.makeText(EditRegisterInfoActivity2.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditRegisterInfoActivity2.this, WelcomeActivity.class));
                            finish();
                        }else if(mistakeInfo.equals("用户名已存在")){
                          Toast.makeText(EditRegisterInfoActivity2.this, "用户名已存在", Toast.LENGTH_SHORT).show();

                        }

                    }
                });


    }

    public void deleteAlbum(int id){
        albumService.deleteAlbum(id, new MyCallBack() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(EditRegisterInfoActivity2.this,"删除成功",Toast.LENGTH_SHORT).show();
                doRequest();
            }

            @Override
            public void onFail(String mistakeInfo) {

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (data != null) {
            if (requestCode == 0 && resultCode == 0) {
                if (data.getStringExtra("body") != null) {

                    currentUserName=data.getStringExtra("body");
                    setUserInfo();
                }
            } else if (requestCode == 1 && resultCode == 1) {
                if (data.getStringExtra("body") != null) {
                    currentUserInfo=data.getStringExtra("body");
                    setUserInfo();

                }
            } else if (requestCode == 3 && resultCode == 3) {
                if (data.getBooleanExtra("sex", true)) {
                    userSex.setText("男");
                    currentUserSex="男";
                } else {
                    currentUserSex="女";

                }
                setUserInfo();
            } else if (requestCode == 2 && resultCode == 2) {

                currentUserHobby=data.getStringExtra("body");
                setUserInfo();
            } else if (requestCode == 4 && resultCode == 4) {
                currentUserSchool=data.getStringExtra("body");
                setUserInfo();
            }
        }


    }


    public void upLoadAlbum(){
        PickService pickService = new PickService(context);
        pickService.pickPhoto(5-mAlbumSize,new PictureConfig.OnSelectResultCallback() {
            @Override
            public void onSelectSuccess(List<LocalMedia> list) {
                OnUploadFinish onUploadFinish = new OnUploadFinish() {
                    @Override
                    public void success() {
                        snackbar.setText("上传成功");
                        snackbar.dismiss();
                        doRequest();
                    }
                    @Override
                    public void failed() {
                        snackbar.setText("上传失败，请重试");
                        snackbar.dismiss();
                        doRequest();
                    }
                };
                UploadAlbumService uploadAlbumService = new UploadAlbumService(context);
                uploadAlbumService.upload(list, onUploadFinish);
                snackbar= SnackBarUtil.getLongTimeInstance(mRecyclerView,"上传中，请稍候...");
                snackbar.show();

            }
            @Override
            public void onSelectSuccess(LocalMedia localMedia) {
            }
        });
    }
    /**
     * 设置年龄对话框
     */
    private void showAgeDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateListener =
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker,
                                          int year, int month, int dayOfMonth) {
                        Calendar mycalendar = Calendar.getInstance();//获取现在时间
                        String nowyear = String.valueOf(mycalendar.get(Calendar.YEAR));//获取年份
                        int age = Integer.parseInt(nowyear);
                        int birth = age - year;
                        currentUserAge="" + birth;
                        setUserInfo();
                    }
                };
        Dialog dialog = new DatePickerDialog(this,
                dateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }
    /**
     * 城市选择Dialog
     */
    private void showCityDialog() {
        CityPicker cityPicker = new CityPicker.Builder(EditRegisterInfoActivity2.this)
                .textSize(20)
                .title("地址选择")
                .backgroundPop(0xa0000000)
                .titleBackgroundColor("#ffffff")
                .titleTextColor("#000000")
                .backgroundPop(0xa0000000)
                .confirTextColor("#000000")
                .cancelTextColor("#000000")
                .province("浙江省")
                .city("杭州市")
                .onlyShowProvinceAndCity(true)
                .textColor(Color.parseColor("#000000"))
                .provinceCyclic(true)
                .cityCyclic(false)
                .districtCyclic(false)
                .visibleItemsCount(7)
                .itemPadding(10)
                .build();
        cityPicker.show();
        //监听方法，获取选择结果
        cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
            @Override
            public void onSelected(String... citySelected) {
                //省份
                String province = citySelected[0];
                //城市
                String city = citySelected[1];
                //区县（如果设定了两级联动，那么该项返回空）
//                String district = citySelected[2];
                //邮编
//                String code = citySelected[3];
                currentUserCity=province + " " + city;

                setUserInfo();
            }

            @Override
            public void onCancel() {
//                Toast.makeText(EditInformationActivity.this, "已取消", Toast.LENGTH_LONG).show();
            }
        });
    }
}