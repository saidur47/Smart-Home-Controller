<?php


function getRoomDetails($cgid,$houseId,$roomId)
{

    require_once "db-connect.php";

    $data["error"] = true;
    $data["message"] = "Unknown error occurred while listing rooms";
    $data["result"]=[];
    try {
        if ($db_connection) {
            $query_get_rooms = $db_connection->prepare('SELECT `room_id`, `house_id`, `name`, `details` FROM `room_list` WHERE  cgid=:cgid and house_id=:hid and room_id=:rid');
            $query_get_rooms->bindParam("cgid", $cgid, PDO::PARAM_STR);
            $query_get_rooms->bindParam("hid", $houseId, PDO::PARAM_STR);
            $query_get_rooms->bindParam("rid", $roomId, PDO::PARAM_STR);
            $query_get_rooms->execute();
            $data["result"]=$query_get_rooms->fetch(PDO::FETCH_ASSOC);
            $data["error"] = false;
            $data["message"] = "Room Details Returned";
        } else {
            $data["message"] = "Couldn't connect to database";
        }
    } catch (Exception $e) {
        $data["message"] = $e->getMessage();
    }

    return $data;
}


function listOfRooms($cgid,$houseId)
{

    require_once "db-connect.php";

    $data["error"] = true;
    $data["message"] = "Unknown error occurred while listing rooms";
    $data["result"]=[];
    try {
        if ($db_connection) {
            $query_get_rooms = $db_connection->prepare('SELECT `room_id`, `house_id`, `name`, `details` FROM `room_list` WHERE  cgid=:cgid and house_id=:hid');
            $query_get_rooms->bindParam("cgid", $cgid, PDO::PARAM_STR);
            $query_get_rooms->bindParam("hid", $houseId, PDO::PARAM_STR);
            $query_get_rooms->execute();
            $data["result"]=$query_get_rooms->fetchAll(PDO::FETCH_ASSOC);
            $data["error"] = false;
            $data["message"] = "List of Rooms Returned";
        } else {
            $data["message"] = "Couldn't connect to database";
        }
    } catch (Exception $e) {
        $data["message"] = $e->getMessage();
    }

    return $data;
}


function addNewRoom($cgid,$hid,$name,$details)
{

    require_once "db-connect.php";

    $data["error"] = true;
    $data["message"] = "Unknown error occurred while adding new room";
    $data["result"]=[];
    try {
        if ($db_connection) {
            $query_cgid_roomcount = $db_connection->prepare('SELECT count(*) as roomCount FROM `room_list` WHERE cgid=:ucgid ');
            $query_cgid_roomcount->bindParam("ucgid", $cgid, PDO::PARAM_STR);
            $query_cgid_roomcount->execute();
            $room_count_result = $query_cgid_roomcount->fetch(PDO::FETCH_ASSOC) ;
            $new_room_id = 1000 * $cgid + $room_count_result["roomCount"]+1;

            $query_house_create =$db_connection->prepare("INSERT INTO `room_list`( `room_id`, `house_id`, `cgid`, `name`, `details`) VALUES (:roomid,:hid,:cgid,:rname,:details)");
            $query_house_create->bindParam("roomid", $new_room_id);
            $query_house_create->bindParam("hid", $hid);
            $query_house_create->bindParam("rname", $name);
            $query_house_create->bindParam("details", $details);
            $query_house_create->bindParam("cgid", $cgid);
            $inserted = $query_house_create->execute();
            if($inserted){
                $data["error"] = false;
                $data["message"]="New Room Created";
            }else{
                $data["message"]="Room Creation Failed";
            }
        } else {
            $data["message"] = "Couldn't connect to database";
        }
    } catch (Exception $e) {
        $data["message"] = $e->getMessage();
    }

    return $data;
}


function updateRoomDetails($cgid,$hid,$rid,$name,$details){
    require_once "db-connect.php";

    $data["error"] = true;
    $data["message"] = "Unknown error occurred while updating device states";
    $data["result"]=[];
    try {
        if ($db_connection) {
            $query_device_update =$db_connection->prepare("UPDATE `room_list` SET `name`=:name, `details`=:details WHERE  `room_id`=:rid and `house_id`= :hid and `cgid`=:cgid");

            $query_device_update->bindParam("rid", $rid, PDO::PARAM_STR);
            $query_device_update->bindParam("hid", $hid, PDO::PARAM_STR);
            $query_device_update->bindParam("cgid", $cgid, PDO::PARAM_STR);
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




function addNewRoomWithDevices($cgid,$hid,$name,$details)
{

    require_once "db-connect.php";

    $data["error"] = true;
    $data["message"] = "Unknown error occurred while adding new room";
    $data["result"]=[];
    try {
        if ($db_connection) {
            $query_cgid_roomcount = $db_connection->prepare('SELECT count(*) as roomCount FROM `room_list` WHERE cgid=:ucgid ');
            $query_cgid_roomcount->bindParam("ucgid", $cgid, PDO::PARAM_STR);
            $query_cgid_roomcount->execute();
            $room_count_result = $query_cgid_roomcount->fetch(PDO::FETCH_ASSOC) ;
            $new_room_id = 1000 * $cgid + $room_count_result["roomCount"]+1;

            $query_room_create =$db_connection->prepare("INSERT INTO `room_list`( `room_id`, `house_id`, `cgid`, `name`, `details`) VALUES (:roomid,:hid,:cgid,:rname,:details)");
            $query_room_create->bindParam("roomid", $new_room_id);
            $query_room_create->bindParam("hid", $hid);
            $query_room_create->bindParam("rname", $name);
            $query_room_create->bindParam("details", $details);
            $query_room_create->bindParam("cgid", $cgid);
            $inserted = $query_room_create->execute();
            if($inserted){
				$query_uid = $db_connection->prepare('SELECT owner_id as uid FROM `client_group` WHERE cgid=:ucgid ');
            $query_uid->bindParam("ucgid", $cgid, PDO::PARAM_STR);
            $query_uid->execute();
			$uidResult = $query_uid->fetch(PDO::FETCH_ASSOC) ;
			$uid=$uidResult["uid"];
				
				
				//
				
				$deviceName="Device-1";
				$deviceDetails="Device-1 Details";
				$new_device_id=1;
				$query_device_create =$db_connection->prepare("INSERT INTO `device_list`(`device_id`, `room_id`, `house_id`, `cgid`, `name`, `details`, `swtich_state`, `lastchangedby`, `lastchangedfrom`, `device_type`) VALUES  (:deviceid,:roomid,:hid,:cgid,:dname,:details,0,:uid,0,0)");
                $query_device_create->bindParam("deviceid", $new_device_id);
                $query_device_create->bindParam("roomid", $new_room_id);
                $query_device_create->bindParam("hid", $hid);
                $query_device_create->bindParam("dname", $deviceName);
                $query_device_create->bindParam("details", $deviceDetails);
                $query_device_create->bindParam("cgid", $cgid);
                $query_device_create->bindParam("uid", $uid);
                $inserted = $query_device_create->execute();
				
				$deviceName="Device-2";
				$deviceDetails="Device-2 Details";
				$new_device_id=2;
				$query_device_create->bindParam("deviceid", $new_device_id);
                $query_device_create->bindParam("dname", $deviceName);
                $query_device_create->bindParam("details", $deviceDetails);
                $inserted = $query_device_create->execute();
				$deviceName="Device-3";
				$deviceDetails="Device-3 Details";
				$new_device_id=3;
				$query_device_create->bindParam("deviceid", $new_device_id);
                $query_device_create->bindParam("dname", $deviceName);
                $query_device_create->bindParam("details", $deviceDetails);
                $inserted = $query_device_create->execute();
				$deviceName="Device-4";
				$deviceDetails="Device-4 Details";
				$new_device_id=4;
				$query_device_create->bindParam("deviceid", $new_device_id);
                $query_device_create->bindParam("dname", $deviceName);
                $query_device_create->bindParam("details", $deviceDetails);
                $inserted = $query_device_create->execute();
				
				
				
                $data["error"] = false;
                $data["message"]="New Room Created";
            }else{
                $data["message"]="Room Creation Failed";
            }
        } else {
            $data["message"] = "Couldn't connect to database";
        }
    } catch (Exception $e) {
        $data["message"] = $e->getMessage();
    }

    return $data;
}



