<?php
use Firebase\JWT\JWT;
use Tuupola\Base62;


function registerNewAdmin($pname,$email,$username,$userPassword){
    require_once "db-connect.php";

    $data["error"] = true;
    $data["message"] = "Unknown error occurred while creating new admin";

    try{
        if($db_connection) {
            $query_user_exist = $db_connection->prepare('SELECT * FROM `admin_details` WHERE adminusername=:uname or adminemail=:uemail');
            $query_user_exist->bindParam("uname", $username, PDO::PARAM_STR);
            $query_user_exist->bindParam("uemail", $email, PDO::PARAM_STR);
            $query_user_exist->execute();

            if($query_user_exist->rowCount() == 0 ){
                $enc_password = password_hash ( $userPassword ,PASSWORD_BCRYPT );

                $query_user_create =$db_connection->prepare("INSERT INTO `admin_details`(`adminname`, `adminemail`, `adminusername`, `adminpassword` ) VALUES( :pname,:uemail,:uname, :upass  )");
                $query_user_create->bindParam("uname", $username);
                $query_user_create->bindParam("upass", $enc_password);
                $query_user_create->bindParam("pname", $pname);
                $query_user_create->bindParam("uemail", $email);

                $inserted = $query_user_create->execute();
                if($inserted){
                    $data["returnObject"] = $db_connection->lastInsertId();
                    $data["returnObject_OTHER"] = $db_connection->prepare("SELECT @newID")->fetch(PDO::FETCH_ASSOC);

                    $data["error"] = false;
                    $data["message"]="New Admin Created";
                }else{
                    $data["error"] = true;
                    $data["message"]="Admin Creation Failed";
                }

            }
            else{
                $data["message"] = "username or email already exist";
            }
        } else {
            $data["message"] = "Couldn't connect to database";
        }
    }
    catch (Exception $e) {
        $data["message"] = $e->getMessage();
    }

    return $data;


}

function authenticateAdminUser($usernameOrEmail, $userPassword)
{

    require_once "db-connect.php";

    $data["error"] = true;
    $data["message"] = "Unknown error occurred while authenticating";

    try {
        if ($db_connection) {
            $query_user_exist = $db_connection->prepare('SELECT * FROM `admin_details` WHERE (`adminusername`= :uname or `adminemail` = :uemail) and  `activation_status`=0 ');
            $query_user_exist->bindParam("uname", $usernameOrEmail, PDO::PARAM_STR);
            $query_user_exist->bindParam("uemail", $usernameOrEmail, PDO::PARAM_STR);
            $query_user_exist->execute();
            $queryResult = $query_user_exist->fetch(PDO::FETCH_ASSOC);
            if ($query_user_exist->rowCount() == 1 && password_verify($userPassword,$queryResult["adminpassword"])) {
                $now = new DateTime();
                $future = new DateTime("now +24 hours");
                $jti = Base62::encode(random_bytes(16));
                $payload = [
                    "iat" => $now->getTimeStamp(),
                    "exp" => $future->getTimeStamp(),
                    "jti" => $jti,
                    "sub" => $queryResult["adminemail"],
                    "userinfo"=>[
                        "username"=>$queryResult["adminusername"],
                        "uid"=>$queryResult["adminid"]
                    ]
                ];
                $secret = "supersecretkeyyoushouldnotcommittogithub";//getenv("JWT_SECRET");

                $token = JWT::encode($payload, $secret, "HS256");
                $data["token"] = $token;
                $data["error"] = false;
                $data["message"] = "Admin Authenticated";

            } else {
                $data["message"] = "Wrong Credential";
            }
        } else {
            $data["message"] = "Couldn't connect to database";
        }
    } catch (Exception $e) {
        $data["message"] = $e->getMessage();
    }

    return $data;
}

function getAdminUserList(){

    require_once "db-connect.php";

    $data["error"] = true;
    $data["message"] = "Unknown error occurred while listing rooms";
    $data["result"]=[];
    try {
        if ($db_connection) {
            $query_get_list = $db_connection->prepare('SELECT `adminname` as name, `adminemail`as email, `adminusername` as username, `activation_status`  FROM `admin_details`');
            $query_get_list->execute();
            $data["result"]=$query_get_list->fetchAll(PDO::FETCH_ASSOC);
            $data["error"] = false;
            $data["message"] = "Admin User List Returned";
        } else {
            $data["message"] = "Couldn't connect to database";
        }
    } catch (Exception $e) {
        $data["message"] = $e->getMessage();
    }

    return $data;
}

function getClientGroupList(){

    require_once "db-connect.php";

    $data["error"] = true;
    $data["message"] = "Unknown error occurred while listing rooms";
    $data["result"]=[];
    try {
        if ($db_connection) {
            $query_get_list = $db_connection->prepare('SELECT G.cgid,D.`name`,c.`email`,G.`activation` FROM `client_group` as G left join `client_credential` as C on G.owner_id = C.userid and G.cgid = C.cgid left join `client_details`as D on C.userid = D.user_id order by C.`userid` desc');
            $query_get_list->execute();
            $data["result"]=$query_get_list->fetchAll(PDO::FETCH_ASSOC);
            $data["error"] = false;
            $data["message"] = "Client User List Returned";
        } else {
            $data["message"] = "Couldn't connect to database";
        }
    } catch (Exception $e) {
        $data["message"] = $e->getMessage();
    }

    return $data;
}

function addNewClient($pname,$email,$username,$userPassword,$auth_code){
    require_once "db-connect.php";

    $data["error"] = true;
    $data["message"] = "Unknown error occurred while creating new user";
    $data["newGroupId"] = -1;
    try{
        if($db_connection) {
            $query_user_exist = $db_connection->prepare('SELECT * FROM `client_credential` WHERE username=:uname or email=:uemail');
            $query_user_exist->bindParam("uname", $username, PDO::PARAM_STR);
            $query_user_exist->bindParam("uemail", $email, PDO::PARAM_STR);
            $query_user_exist->execute();

            if($query_user_exist->rowCount() == 0 ){
                $query_cgid = $db_connection->prepare('SELECT (1+MAX(cgid)) as newCgid  FROM `client_group`');
                $query_cgid->execute();
                $newClientGroupIdResult = $query_cgid->fetch(PDO::FETCH_ASSOC) ;
                $newClientGroupId=$newClientGroupIdResult["newCgid"];
                $query_cgid_create =$db_connection->prepare("INSERT INTO `client_group`(`cgid`, `activation`) VALUES ( :cgid, :authcode)");
                $query_cgid_create->bindParam("cgid", $newClientGroupId);
                $query_cgid_create->bindParam("authcode", $auth_code);
                $inserted = $query_cgid_create->execute();
                if($inserted){
                    $client_group_table_id= $db_connection->lastInsertId();

//
                    $query_cgid_usercount = $db_connection->prepare('SELECT count(*) as userCount FROM `client_credential` WHERE cgid=:ucgid ');
                    $query_cgid_usercount->bindParam("ucgid", $newClientGroupId, PDO::PARAM_STR);
                    $query_cgid_usercount->execute();
                    $user_count_result = $query_cgid_usercount->fetch(PDO::FETCH_ASSOC) ;
                    $new_user_id = 1000 * $newClientGroupId+$user_count_result["userCount"]+1;

                    $user_type = '0';
                    $user_status = '0';
                    $enc_password = password_hash ( $userPassword ,PASSWORD_BCRYPT );

                    $query_user_create =$db_connection->prepare("INSERT INTO `client_credential`(`userid`, `username`, `password`, `email`, `cgid`, `usertype`, `activation_status`) VALUES( :uid, :uname, :upass, :uemail, :ucgid, :utype, :ustatus)");
                    $query_user_create->bindParam("uid", $new_user_id);
                    $query_user_create->bindParam("uname", $username);
                    $query_user_create->bindParam("upass", $enc_password);
                    $query_user_create->bindParam("uemail", $email);
                    $query_user_create->bindParam("ucgid", $newClientGroupId);
                    $query_user_create->bindParam("utype", $user_type);
                    $query_user_create->bindParam("ustatus", $user_status);

                    $inserted = $query_user_create->execute();
                    if($inserted){
                        $data["returnObject"] = $db_connection->lastInsertId();
                        $data["returnObject_OTHER"] = $db_connection->prepare("SELECT @newID")->fetch(PDO::FETCH_ASSOC);

                        $query_user_details =$db_connection->prepare("INSERT INTO `client_details`(`user_id`, `name`, `address`) VALUES (:uid,:pname,'')");
                        $query_user_details->bindParam("uid", $new_user_id);
                        $query_user_details->bindParam("pname", $pname);
                        $query_user_details->execute();

                        $data["error"] = false;
                        $data["message"]="New user created";
                        $data["newUid"] =$new_user_id;
                        $data["newGroupId"]=$newClientGroupId;


                        $query_ownerid_update = $db_connection->prepare('UPDATE `client_group` SET `owner_id`=:ownerid where `cgid`=:upcgid');
                        $query_ownerid_update->bindParam("ownerid", $new_user_id);
                        $query_ownerid_update->bindParam("upcgid", $newClientGroupId);
                        $query_ownerid_update->execute();
                        $data["error"] = false;
                        $data["newGroupId"]=$newClientGroupId;
                    }else{
                        $data["error"] = true;
                        $data["message"]="User Creation Failed for client group";
                    }


                    //


                }else{
                    $data["error"] = true;
                    $data["message"]="Client Group Creation Failed";
                }

            }
            else{
                $data["message"] = "username or email already exist";
            }
        } else {
            $data["message"] = "Couldn't connect to database";
        }
    }
    catch (Exception $e) {
        $data["message"] = $e->getMessage();
    }

    return $data;
}



function registerNewUserForClient($db_connection,$pname,$username,$userPassword,$email,$groupId,$auth_code){
    $data["error"] = true;
    $data["message"] = "Unknown error occurred while creating new user";

    try{
        if($db_connection) {
            $query_user_exist = $db_connection->prepare('SELECT * FROM `client_credential` WHERE username=:uname or email=:uemail');
            $query_user_exist->bindParam("uname", $username, PDO::PARAM_STR);
            $query_user_exist->bindParam("uemail", $email, PDO::PARAM_STR);
            $query_user_exist->execute();

            if($query_user_exist->rowCount() == 0 ){
                $query_cgid_auth = $db_connection->prepare('SELECT count(*) as groupCount FROM `client_group` WHERE cgid=:ucgid and activation=:uauthcode');
                $query_cgid_auth->bindParam("ucgid", $groupId, PDO::PARAM_STR);
                $query_cgid_auth->bindParam("uauthcode", $auth_code, PDO::PARAM_STR);
                $query_cgid_auth->execute();
                $cgid_count_result = $query_cgid_auth->fetch(PDO::FETCH_ASSOC) ;

                if($cgid_count_result["groupCount"] == 1 ){
                    $query_cgid_usercount = $db_connection->prepare('SELECT count(*) as userCount FROM `client_credential` WHERE cgid=:ucgid ');
                    $query_cgid_usercount->bindParam("ucgid", $groupId, PDO::PARAM_STR);
                    $query_cgid_usercount->execute();
                    $user_count_result = $query_cgid_usercount->fetch(PDO::FETCH_ASSOC) ;
                    $new_user_id = 1000 * $groupId+$user_count_result["userCount"]+1;

                    $user_type = '0';
                    $user_status = '0';
                    $enc_password = password_hash ( $userPassword ,PASSWORD_BCRYPT );

                    $query_user_create =$db_connection->prepare("INSERT INTO `client_credential`(`userid`, `username`, `password`, `email`, `cgid`, `usertype`, `activation_status`) VALUES( :uid, :uname, :upass, :uemail, :ucgid, :utype, :ustatus)");
                    $query_user_create->bindParam("uid", $new_user_id);
                    $query_user_create->bindParam("uname", $username);
                    $query_user_create->bindParam("upass", $enc_password);
                    $query_user_create->bindParam("uemail", $email);
                    $query_user_create->bindParam("ucgid", $groupId);
                    $query_user_create->bindParam("utype", $user_type);
                    $query_user_create->bindParam("ustatus", $user_status);

                    $inserted = $query_user_create->execute();
                    if($inserted){
                        $query_user_details =$db_connection->prepare("INSERT INTO `client_details`(`user_id`, `name`, `address`) VALUES (:uid,:pname,'')");
                        $query_user_details->bindParam("uid", $new_user_id);
                        $query_user_details->bindParam("pname", $pname);
                        $query_user_details->execute();

                        $data["error"] = false;
                        $data["message"]="New user created";
                        $data["newUid"] =$new_user_id;
                    }else{
                        $data["error"] = true;
                        $data["message"]="User Creation Failed";
                    }

                }
                else {
                    $data["message"] = "Group information incorrect";
                }
            }
            else{
                $data["message"] = "username or email already exist";
            }
        } else {
            $data["message"] = "Couldn't connect to database";
        }
    }
    catch (Exception $e) {
        $data["message"] = $e->getMessage();
    }

    return $data;


}

function deleteClient($cgid){
    require_once "db-connect.php";

    $data["error"] = true;
    $data["message"] = "Unknown error occurred while listing rooms";
    $data["result"]=[];
    try {
        if ($db_connection) {
            $query_delete = $db_connection->prepare('DELETE FROM `client_group` WHERE `cgid`=:cgid');
            $query_delete->bindParam("cgid", $cgid, PDO::PARAM_STR);
            $data["cgidDeleted"] = $query_delete->execute();

            $query_delete_users = $db_connection->prepare('DELETE FROM `client_credential` WHERE `cgid`=:cgid');
            $query_delete_users->bindParam("cgid", $cgid, PDO::PARAM_STR);
            $data["cgidUserDeleted"] = $query_delete_users->execute();

            $data["error"] = false;
            $data["message"] = "Deleted";
        } else {
            $data["message"] = "Couldn't connect to database";
        }
    } catch (Exception $e) {
        $data["message"] = $e->getMessage();
    }

    return $data;
}
