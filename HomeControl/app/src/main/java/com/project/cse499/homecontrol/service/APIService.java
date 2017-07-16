package  com.project.cse499.homecontrol;

import android.content.SharedPreferences;

import android.support.v7.app.AppCompatActivity;

import com.project.cse499.homecontrol.Activities.Devices.DeviceLog;
import com.project.cse499.homecontrol.helpers.CommonHelpers;
import com.project.cse499.homecontrol.model.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface APIService {
    /*
    * Used in Sign-in Page
    * */
    @FormUrlEncoded
    @POST("/m/login")
    Call<AuthenticatedUser> authenticateUser(
            @Field("username") String uname,
            @Field("password") String upass
    );
    /*
    * Used in Register Page
    * */
    @FormUrlEncoded
    @POST("/m/register")
    Call<BaseResponse> registerUser(
            @Field("name") String name,
            @Field("username") String uname,
            @Field("email") String email,
            @Field("password") String pass,
            @Field("groupid") int groupid,
            @Field("authcode") int authcode
    );

    /*
  ** Used in House's List Page
  */
    @GET("/m/getListOfHouses")
    Call<List<House>> getListOfHouses(
            @Header("Authorization") String jwtTokenValue
    );

    /*
    ** Used in Room's List Page
    */
    @GET("/m/getListOfRooms")
    Call<List<Room>> getListOfRooms(
            @Header("Authorization") String jwtTokenValue,
            @Header("houseid") int houseId
    );


    /*
    ** Used in Room's Device List Page
    */
    @GET("/m/getListOfDevices")
    Call<List<Device>> getListOfDevices(
            @Header("Authorization") String jwtTokenValue,
            @Header("houseid") int houseid,
            @Header("roomid") int roomid
    );

    @FormUrlEncoded
    @PUT("/m/updateDevicesState")
    Call<DevicesSatesUpdate> updateDeviceStates(
            @Header("Authorization") String jwtTokenValue,
            @Field("houseid") int houseid,
            @Field("roomid") int roomid,
            @Field("device1") int  dval1,
            @Field("device2") int  dval2,
            @Field("device3") int  dval3,
            @Field("device4") int  dval4
    );


    @GET("/m/getDeviceDetails")
    Call<Device> getDeviceDetails(
            @Header("Authorization") String jwtTokenValue,
            @Header("houseid") int houseid,
            @Header("roomid") int roomid,
            @Header("deviceid") int deviceid
    );
    @FormUrlEncoded
    @PUT("/m/updateDeviceDetails")
    Call<UpdateDevice> updateDeviceDetails(
            @Header("Authorization") String jwtTokenValue,
            @Field("houseid") int houseid,
            @Field("roomid") int roomid,
            @Field("deviceid") int deviceid,
            @Field("device_name") String  name,
            @Field("device_details") String details
    );
    ///
    @GET("/m/getRoomDetails")
    Call<Room> getRoomDetails(
            @Header("Authorization") String jwtTokenValue,
            @Header("houseid") int houseid,
            @Header("roomid") int roomid
    );
    @FormUrlEncoded
    @PUT("/m/updateRoomDetails")
    Call<Room> updateRoomDetails(
            @Header("Authorization") String jwtTokenValue,
            @Field("houseid") int houseid,
            @Field("roomid") int roomid,
            @Field("name") String  name,
            @Field("details") String details
    );
    @FormUrlEncoded
    @PUT("/m/changePassword")
    Call<BaseResponse> changePassword(
            @Header("Authorization") String jwtTokenValue,
            @Field("username") String  username,
            @Field("old_pass") String oldPass,
            @Field("new_pass") String  newPass
    );

    @GET("/m/getDeviceLog")
    Call<List<DeviceLogEntry>> getDeviceLog(
            @Header("Authorization") String jwtTokenValue,
            @Header("houseid") int houseid,
            @Header("roomid") int roomid
    );
}
