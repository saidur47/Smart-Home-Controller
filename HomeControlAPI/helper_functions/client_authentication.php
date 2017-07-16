<?php
use Firebase\JWT\JWT;
use Tuupola\Base62;

function registerNewUser($pname,$username,$userPassword,$email,$groupId,$auth_code){
    require_once "db-connect.php";

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
                        $data["returnObject"] = $db_connection->lastInsertId();
                        $data["returnObject_OTHER"] = $db_connection->prepare("SELECT @newID")->fetch(PDO::FETCH_ASSOC);

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

function authenticateUser($usernameOrEmail, $userPassword)
{

    require_once "db-connect.php";

    $data["error"] = true;
    $data["message"] = "Unknown error occurred while authenticating";

    try {
        if ($db_connection) {
            $query_user_exist = $db_connection->prepare('SELECT * FROM `client_credential` WHERE (username=:uname or email=:uemail)');
            $query_user_exist->bindParam("uname", $usernameOrEmail, PDO::PARAM_STR);
            $query_user_exist->bindParam("uemail", $usernameOrEmail, PDO::PARAM_STR);
            $query_user_exist->execute();
            $queryResult = $query_user_exist->fetch(PDO::FETCH_ASSOC);
            if ($query_user_exist->rowCount() == 1 && password_verify($userPassword,$queryResult["password"])) {
                $now = new DateTime();
                $future = new DateTime("now +24 hours");
                $jti = Base62::encode(random_bytes(16));
                $payload = [
                    "iat" => $now->getTimeStamp(),
                    "exp" => $future->getTimeStamp(),
                    "jti" => $jti,
                    "sub" => $queryResult["email"],
                    "userinfo"=>[
                        "username"=>$queryResult["username"],
                        "uid"=>$queryResult["userid"],
                        "cgid"=>$queryResult["cgid"]
                    ]
                ];
                $secret = "supersecretkeyyoushouldnotcommittogithub";//getenv("JWT_SECRET");

                $token = JWT::encode($payload, $secret, "HS256");
                $data["token"] = $token;
                $data["error"] = false;
                $data["message"] = "User Authenticated";

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

function changePassword($cgid,$user_name,$old_pass,$new_pass){
    require_once "db-connect.php";

    $data["error"] = true;
    $data["message"] = "Unknown error occurred while authenticating";

    try {
        if ($db_connection) {
            $query_user_exist = $db_connection->prepare('SELECT * FROM `client_credential` WHERE (username=:uname)');
            $query_user_exist->bindParam("uname", $user_name, PDO::PARAM_STR);
            $query_user_exist->execute();
            $queryResult = $query_user_exist->fetch(PDO::FETCH_ASSOC);
            if ($query_user_exist->rowCount() == 1 && password_verify($old_pass,$queryResult["password"])) {
                $query_password_update =$db_connection->prepare("UPDATE `client_credential` SET `password`=:pass WHERE(username=:uname) and `cgid`=:cgid");
                $query_password_update->bindParam("cgid", $cgid, PDO::PARAM_STR);
                $query_password_update->bindParam("uname", $user_name, PDO::PARAM_STR);
                $enc_password = password_hash ( $new_pass ,PASSWORD_BCRYPT );
                $query_password_update->bindParam("pass",$enc_password, PDO::PARAM_INT);
                $query_password_update->execute();
                $data["error"] = false;
                $data["message"]="successful";
            }
        } else {
            $data["message"] = "Couldn't connect to database";
        }
    } catch (Exception $e) {
        $data["message"] = $e->getMessage();
    }

    return $data;
}

