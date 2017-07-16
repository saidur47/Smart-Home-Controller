<?php

function listOfHouses($cgid)
{

    require_once "db-connect.php";

    $data["error"] = true;
    $data["message"] = "Unknown error occurred while listing houses";
    $data["result"]=[];
    $data["cgid"]=$cgid;
    try {
        if ($db_connection) {
            $query_get_houses = $db_connection->prepare('SELECT `house_id`, `name`, `details` FROM `house_list` WHERE cgid=:cgid ');
            $query_get_houses->bindParam("cgid", $cgid, PDO::PARAM_INT);
            $query_get_houses->execute();

            $data["result"]=$query_get_houses->fetchAll(PDO::FETCH_ASSOC);
            $data["error"] = false;
            $data["message"] = "List of Houses Returnd";
        } else {
            $data["message"] = "Couldn't connect to database";
        }
    } catch (Exception $e) {
        $data["message"] = $e->getMessage();
    }

    return $data;
}

function addNewHouse($cgid,$name,$details)
{

    require_once "db-connect.php";

    $data["error"] = true;
    $data["message"] = "Unknown error occurred while adding new house";
    $data["result"]=[];
    try {
        if ($db_connection) {
            $query_cgid_housecount = $db_connection->prepare('SELECT count(*) as houseCount FROM `house_list` WHERE cgid=:ucgid ');
            $query_cgid_housecount->bindParam("ucgid", $cgid, PDO::PARAM_STR);
            $query_cgid_housecount->execute();
            $house_count_result = $query_cgid_housecount->fetch(PDO::FETCH_ASSOC) ;
            $new_house_id = 1000 * $cgid + $house_count_result["houseCount"]+1;

            $query_house_create =$db_connection->prepare("INSERT INTO `house_list`( `house_id`, `cgid`, `name`, `details`) VALUES (:hid,:cgid,:hname,:details)");
            $query_house_create->bindParam("hid", $new_house_id);
            $query_house_create->bindParam("hname", $name);
            $query_house_create->bindParam("details", $details);
            $query_house_create->bindParam("cgid", $cgid);
            $inserted = $query_house_create->execute();
            if($inserted){
                $data["error"] = false;
                $data["message"]="New house created";
            }else{
                $data["message"]="House Creation Failed";
            }
        } else {
            $data["message"] = "Couldn't connect to database";
        }
    } catch (Exception $e) {
        $data["message"] = $e->getMessage();
    }

    return $data;
}

