<?php
function getDevice($cgid,$houseId,$roomId,$deviceId)
{

    require_once "db-connect.php";

    $data["error"] = true;
    $data["message"] = "Unknown error occurred while listing rooms";
    $data["result"]=[];
    try {
        if ($db_connection) {
            $query_get_devices = $db_connection->prepare('SELECT `device_id`, `room_id`, `house_id`, `name`, `details`, `swtich_state`, `lastchangedby`, `lastchangedfrom`, `device_type`  FROM `device_list` WHERE  cgid=:cgid and house_id=:hid and room_id=:rid and device_id=:did');
            $query_get_devices->bindParam("cgid", $cgid, PDO::PARAM_STR);
            $query_get_devices->bindParam("hid", $houseId, PDO::PARAM_STR);
            $query_get_devices->bindParam("rid", $roomId, PDO::PARAM_STR);
            $query_get_devices->bindParam("did", $deviceId, PDO::PARAM_STR);
            $query_get_devices->execute();
            $data["result"]=$query_get_devices->fetch(PDO::FETCH_ASSOC);
            $data["error"] = false;
            $data["message"] = "Device Details Returned";
        } else {
            $data["message"] = "Couldn't connect to database";
        }
    } catch (Exception $e) {
        $data["message"] = $e->getMessage();
    }

    return $data;
}


function listOfDevices($cgid,$houseId,$roomId)
{

    require_once "db-connect.php";

    $data["error"] = true;
    $data["message"] = "Unknown error occurred while listing rooms";
    $data["result"]=[];
    try {
        if ($db_connection) {
            $query_get_devices = $db_connection->prepare('SELECT `device_id`, `room_id`, `house_id`, `name`, `details`, `swtich_state`, `lastchangedby`, `lastchangedfrom`, `device_type`  FROM `device_list` WHERE  cgid=:cgid and house_id=:hid and room_id=:rid');
            $query_get_devices->bindParam("cgid", $cgid, PDO::PARAM_STR);
            $query_get_devices->bindParam("hid", $houseId, PDO::PARAM_STR);
            $query_get_devices->bindParam("rid", $roomId, PDO::PARAM_STR);
            $query_get_devices->execute();
            $data["result"]=$query_get_devices->fetchAll(PDO::FETCH_ASSOC);
            $data["error"] = false;
            $data["message"] = "List of Device Returned";
        } else {
            $data["message"] = "Couldn't connect to database";
        }
    } catch (Exception $e) {
        $data["message"] = $e->getMessage();
    }

    return $data;
}


function addNewDevice($cgid,$uid,$hid,$rid,$name,$details)
{

    require_once "db-connect.php";

    $data["error"] = true;
    $data["message"] = "Unknown error occurred while adding new room";
    $data["result"]=[];
    try {
        if ($db_connection) {
            $query_cgid_devicecount = $db_connection->prepare('SELECT count(*) as deviceCount FROM `device_list` WHERE cgid=:ucgid and room_id = :urid and house_id = :uhid ');
            $query_cgid_devicecount->bindParam("ucgid", $cgid, PDO::PARAM_STR);
            $query_cgid_devicecount->bindParam("urid", $rid, PDO::PARAM_STR);
            $query_cgid_devicecount->bindParam("uhid", $hid, PDO::PARAM_STR);
            $query_cgid_devicecount->execute();
            $device_count_result = $query_cgid_devicecount->fetch(PDO::FETCH_ASSOC) ;
            $new_device_id = $device_count_result["deviceCount"]+1;

            if($new_device_id<5){

                $query_device_create =$db_connection->prepare("INSERT INTO `device_list`(`device_id`, `room_id`, `house_id`, `cgid`, `name`, `details`, `swtich_state`, `lastchangedby`, `lastchangedfrom`, `device_type`) VALUES  (:deviceid,:roomid,:hid,:cgid,:dname,:details,0,:uid,0,0)");
                $query_device_create->bindParam("deviceid", $new_device_id);
                $query_device_create->bindParam("roomid", $rid);
                $query_device_create->bindParam("hid", $hid);
                $query_device_create->bindParam("dname", $name);
                $query_device_create->bindParam("details", $details);
                $query_device_create->bindParam("cgid", $cgid);
                $query_device_create->bindParam("uid", $uid);
                $inserted = $query_device_create->execute();
                if($inserted){
                    $data["error"] = false;
                    $data["message"]="New Device Created";
                }else{
                    $data["message"]="Room Creation Failed";
                }
            }else{
                $data["message"]="No More Than 4 Devices Per Room";
            }
        } else {
            $data["message"] = "Couldn't connect to database";
        }
    } catch (Exception $e) {
        $data["message"] = $e->getMessage();
    }

    return $data;
}

function updateDeviceDetails($cgid,$uid,$hid,$rid,$did,$name,$details,$source){
    require_once "db-connect.php";

    $data["error"] = true;
    $data["message"] = "Unknown error occurred while updating device states";
    $data["result"]=[];
    try {
        if ($db_connection) {
            $query_device_update =$db_connection->prepare("UPDATE `device_list` SET `name`=:name, `details`=:details,`lastchangedby`=:uid,`lastchangedfrom`=:source WHERE `device_id`=:did and `room_id`=:rid and `house_id`= :hid and `cgid`=:cgid");

            $query_device_update->bindParam("rid", $rid, PDO::PARAM_STR);
            $query_device_update->bindParam("hid", $hid, PDO::PARAM_STR);
            $query_device_update->bindParam("cgid", $cgid, PDO::PARAM_STR);
            $query_device_update->bindParam("uid", $uid, PDO::PARAM_STR);
            $query_device_update->bindParam("did", $did, PDO::PARAM_STR);
            $query_device_update->bindParam("source", $source, PDO::PARAM_STR);
            $query_device_update->bindParam("name", $name, PDO::PARAM_STR);
            $query_device_update->bindParam("details", $details, PDO::PARAM_STR);
            $query_device_update->execute();

            $data["error"] = false;
            $data["message"]="Updated"+$name;


        } else {
            $data["message"] = "Couldn't connect to database";
        }
    } catch (Exception $e) {
        $data["message"] = $e->getMessage();
    }

    return $data;
}



function updateDeviceStates($cgid,$uid,$hid,$rid,$states,$source){
    require_once "db-connect.php";

    $data["error"] = true;
    $data["message"] = "Unknown error occurred while updating device states";
    $data["result"]=[];
    try {
        if ($db_connection) {
            $query_device_update =$db_connection->prepare("UPDATE `device_list` SET `swtich_state`=:switchState,`lastchangedby`=:uid,`lastchangedfrom`=:source WHERE `device_id`=:did and `room_id`=:rid and `house_id`= :hid and `cgid`=:cgid");
            $query_device_log =$db_connection->prepare("INSERT INTO `device_change_log` (`cgid`, `user_id`, `house_id`, `room_id`, `device_id`, `status`) VALUES (:lcgid,:luid ,:lhid, :lrid, :ldid ,:lstatus)");

            $did = 1;
            $query_device_update->bindParam("rid", $rid, PDO::PARAM_STR);
            $query_device_update->bindParam("hid", $hid, PDO::PARAM_STR);
            $query_device_update->bindParam("cgid", $cgid, PDO::PARAM_STR);
            $query_device_update->bindParam("uid", $uid, PDO::PARAM_STR);
            $query_device_update->bindParam("source", $source, PDO::PARAM_STR);
            $query_device_update->bindParam("did", $did, PDO::PARAM_INT);
            $query_device_update->bindParam("switchState", $states[0], PDO::PARAM_INT);
            $query_device_update->execute();

            $query_device_log->bindParam("lrid", $rid, PDO::PARAM_STR);
            $query_device_log->bindParam("lhid", $hid, PDO::PARAM_STR);
            $query_device_log->bindParam("lcgid", $cgid, PDO::PARAM_STR);
            $query_device_log->bindParam("luid", $uid, PDO::PARAM_STR);
            $query_device_log->bindParam("ldid", $did, PDO::PARAM_INT);
            $query_device_log->bindParam("lstatus", $states[0], PDO::PARAM_INT);
            $query_device_log->execute();

            $did = 2;
            $query_device_update->bindParam("did", $did, PDO::PARAM_INT);
            $query_device_update->bindParam("switchState", $states[1], PDO::PARAM_INT);
            $query_device_update->execute();

            $query_device_log->bindParam("ldid", $did, PDO::PARAM_INT);
            $query_device_log->bindParam("lstatus", $states[1], PDO::PARAM_INT);
            $query_device_log->execute();

            $did = 3;
            $query_device_update->bindParam("did", $did, PDO::PARAM_INT);
            $query_device_update->bindParam("switchState", $states[2], PDO::PARAM_INT);
            $query_device_update->execute();

            $query_device_log->bindParam("ldid", $did, PDO::PARAM_INT);
            $query_device_log->bindParam("lstatus", $states[2], PDO::PARAM_INT);
            $query_device_log->execute();

            $did = 4;
            $query_device_update->bindParam("did", $did, PDO::PARAM_INT);
            $query_device_update->bindParam("switchState", $states[3], PDO::PARAM_INT);
            $query_device_update->execute();

            $query_device_log->bindParam("ldid", $did, PDO::PARAM_INT);
            $query_device_log->bindParam("lstatus", $states[3], PDO::PARAM_INT);
            $query_device_log->execute();

            $data["error"] = false;
            $data["message"]="Updated";


        } else {
            $data["message"] = "Couldn't connect to database";
        }
    } catch (Exception $e) {
        $data["message"] = $e->getMessage();
    }

    return $data;
}


function getDeviceLog($cgid,$houseId,$roomId){
    require_once "db-connect.php";

    $data["error"] = true;
    $data["message"] = "Unknown error occurred while listing rooms";
    $data["result"]=[];
    try {
        if ($db_connection) {
            $query_get_devices_log = $db_connection->prepare('SELECT d.name as device, u.name as user, l.status as action, l.log_time as logtime FROM `device_change_log` AS l left join `device_list` as d ON l.device_id = d.device_id and d.house_id=:dhid and d.room_id=:drid and d.cgid = :dcgid left join `client_credential` as c ON l.user_id = c.userid left join `client_details` as u ON c.userid = u.user_id WHERE l.cgid=:cgid and l.house_id=:hid and l.room_id=:rid ORDER by l.id desc');
            $query_get_devices_log->bindParam("cgid", $cgid, PDO::PARAM_STR);
            $query_get_devices_log->bindParam("hid", $houseId, PDO::PARAM_STR);
            $query_get_devices_log->bindParam("rid", $roomId, PDO::PARAM_STR);
            $query_get_devices_log->bindParam("dcgid", $cgid, PDO::PARAM_STR);
            $query_get_devices_log->bindParam("dhid", $houseId, PDO::PARAM_STR);
            $query_get_devices_log->bindParam("drid", $roomId, PDO::PARAM_STR);
            $data["eresult"]=array($cgid,$houseId,$roomId,$query_get_devices_log);
            $query_get_devices_log->execute();
            $data["result"]=$query_get_devices_log->fetchAll(PDO::FETCH_ASSOC);


            $data["error"] = false;
            $data["message"] = "List of Device Returned";
        } else {
            $data["message"] = "Couldn't connect to database";
        }
    } catch (Exception $e) {
        $data["message"] = $e->getMessage();
    }

    return $data;
}