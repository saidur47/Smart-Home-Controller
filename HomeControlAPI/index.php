<?php

use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;

require 'vendor/autoload.php';

$config['displayErrorDetails'] = true;
$config['addContentLengthHeader'] = false;

$app = new \Slim\App(["settings" => $config]);

$container = $app->getContainer();

$container["jwt"] = function ($c) {
    return new StdClass;
};

$app->add(new \Slim\Middleware\JwtAuthentication(array(
    "secure" => false,
    "path" => array("/m", "/a"),
    "passthrough" => array("/m/login", "/m/register","/d","/a/login", "/a/register"),
    "secret" => "supersecretkeyyoushouldnotcommittogithub",
    "callback" => function ($request, $response, $arguments) use ($container) {
        $container["jwt"] = $arguments["decoded"];
    },
    "error" => function ($request, $response, $arguments) {
        $data["error"] = true;
        $data["message"] = $arguments["message"];
        return $response
            ->withHeader("Content-Type", "application/json")
            ->write(json_encode($data, JSON_UNESCAPED_SLASHES | JSON_PRETTY_PRINT));
    }
)));
$app->add(function ($req, $res, $next) {
    $response = $next($req, $res);
    return $response
        ->withHeader('Access-Control-Allow-Origin', 'http://localhost:8000')
        ->withHeader('Access-Control-Allow-Headers', 'X-Requested-With, Content-Type, Accept, Origin, Authorization','cgid','hid','rid','did')
        ->withHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
});

/**
 * Load Routing Helper Function's Definitions
 **/
foreach (glob("helper_functions/*.php") as $filename)
{
    require_once $filename;
}


/**
 * Mobile : Register New User
 */
$app->post("/m/register",function (Request $request,Response $response, $args){
    $request_body = $request->getParsedBody();
    $request_name = $request_body['name'];
    $request_uname = $request_body['username'];
    $request_pass=$request_body['password'];
    $request_email = $request_body['email'];
    $request_groupid=$request_body['groupid'];
    $request_auth_code = $request_body['authcode'];
    $responseObject = registerNewUser( $request_name,$request_uname,$request_pass,$request_email ,$request_groupid,$request_auth_code);
    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($responseObject));
});
/**
 * Mobile: Sign in user
 */
$app->post("/m/login",function (Request $request,Response $response, $args){
    $request_body = $request->getParsedBody();
    $request_uname = $request_body['username'];
    $request_pass=$request_body['password'];
    $responseObject = authenticateUser( $request_uname,$request_pass);
    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($responseObject));
});

/**
 * Mobile: Add New House
 */
$app->post("/m/addHouse",function (Request $request,Response $response, $args){
    $cgid = $this->jwt->userinfo->cgid;

    $request_body = $request->getParsedBody();
    $request_name = $request_body['name'];
    $request_details=$request_body['details'];

    $dbResponse = addNewHouse($cgid,$request_name,$request_details);

    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($dbResponse));
});
/**
 * Mobile: Get List Of Houses
 */
$app->get("/m/getListOfHouses",function (Request $request,Response $response, $args){
    $cgid = $this->jwt->userinfo->cgid;
    $dbResponse = listOfHouses($cgid);
    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($dbResponse["result"]));
});


/**
 * Mobile: Add New Room
 */
$app->post("/m/addRoom",function (Request $request,Response $response, $args){
    $cgid = $this->jwt->userinfo->cgid;

    $request_body = $request->getParsedBody();
    $request_house= $request_body['houseid'];
    $request_name = $request_body['name'];
    $request_details=$request_body['details'];

    $dbResponse = addNewRoom($cgid,$request_house,$request_name,$request_details);

    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($dbResponse));
});
/**
 * Mobile: Get List Of Rooms
 */
$app->get("/m/getListOfRooms",function (Request $request,Response $response, $args){
    $cgid = $this->jwt->userinfo->cgid;
    $request_house=  $request->getHeader('houseid')[0];
    $dbResponse = listOfRooms($cgid,$request_house);
    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($dbResponse["result"]));
});
/**
 * Mobile: Get details of  Rooms
 */
$app->get("/m/getRoomDetails",function (Request $request,Response $response, $args){
    $cgid = $this->jwt->userinfo->cgid;
    $request_house=  $request->getHeader('houseid')[0];
    $request_room=  $request->getHeader('roomid')[0];
    $dbResponse = getRoomDetails($cgid,$request_house,$request_room);
    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($dbResponse["result"]));
});

$app->put("/m/updateRoomDetails",function (Request $request,Response $response, $args){
    $cgid = $this->jwt->userinfo->cgid;
    $request_body = $request->getParsedBody();
    $request_house=  $request_body['houseid'];
    $request_room=$request_body['roomid'];
    $request_name=$request_body['name'];
    $request_details= $request_body['details'];

    $dbResponse = updateRoomDetails($cgid,$request_house,$request_room,$request_name,$request_details);
    $statusCode = 200;
    if($dbResponse["error"]){
        $statusCode=301;
    }
    return $response->withStatus($statusCode)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($dbResponse));
});

/**
 * Mobile: Add new Device in the room
 */
$app->post("/m/addDevice",function (Request $request,Response $response, $args){
    $cgid = $this->jwt->userinfo->cgid;
    $uid  = $this->jwt->userinfo->uid;
    $request_body = $request->getParsedBody();
    $request_name=  $request_body['name'];
    $request_details=  $request_body['details'];
    $request_house=  $request_body['houseid'];
    $request_room=$request_body['roomid'];
    $dbResponse = addNewDevice($cgid,$uid,$request_house,$request_room,$request_name,$request_details);

    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($dbResponse));
});
/**
 * Mobile: Get List Of Devices With Status
 */
$app->get("/m/getListOfDevices",function (Request $request,Response $response, $args){
    $cgid = $this->jwt->userinfo->cgid;
    $request_house=  $request->getHeader('houseid')[0];
    $request_room=  $request->getHeader('roomid')[0];

    $dbResponse = listOfDevices($cgid,$request_house,$request_room);

    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($dbResponse["result"]));
});
/**
 * Mobile: Get Details of A Device
 */
$app->get("/m/getDeviceDetails",function (Request $request,Response $response, $args){
    $cgid = $this->jwt->userinfo->cgid;
    $request_house=  $request->getHeader('houseid')[0];
    $request_room=  $request->getHeader('roomid')[0];
    $request_device=  $request->getHeader('deviceid')[0];

    $dbResponse = getDevice($cgid,$request_house,$request_room,$request_device);

    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($dbResponse["result"]));
});
/**
 * Mobile: Update Device State
 */
$app->put("/m/updateDeviceDetails",function (Request $request,Response $response, $args){
    $cgid = $this->jwt->userinfo->cgid;
    $uid  = $this->jwt->userinfo->uid;
    $request_body = $request->getParsedBody();
    $request_house=  $request_body['houseid'];
    $request_room=$request_body['roomid'];
    $request_device=$request_body['deviceid'];
    $request_name=$request_body['device_name'];
    $request_details= $request_body['device_details'];

    $dbResponse = updateDeviceDetails($cgid,$uid,$request_house,$request_room,$request_device,$request_name,$request_details,0);
    $statusCode = 200;
    if($dbResponse["error"]){
        $statusCode=301;
    }
    return $response->withStatus($statusCode)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($dbResponse));
});
/*
 * Mobile: device log get
 */
$app->get("/m/getDeviceLog",function (Request $request,Response $response, $args){
    $cgid = $this->jwt->userinfo->cgid;
    $request_house=  $request->getHeader('houseid')[0];
    $request_room=  $request->getHeader('roomid')[0];

    $dbResponse = getDeviceLog($cgid,$request_house,$request_room);

    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($dbResponse["result"]));
});
/**
 * Mobile: Update switch Status of  Device
 */
$app->put("/m/updateDevicesState",function (Request $request,Response $response, $args){
    $cgid = $this->jwt->userinfo->cgid;
    $uid  = $this->jwt->userinfo->uid;
    $request_body = $request->getParsedBody();
    $request_house=  $request_body['houseid'];
    $request_room=$request_body['roomid'];
    $request_states=array($request_body['device1'],$request_body['device2'],$request_body['device3'],$request_body['device4']);

    $dbResponse = updateDeviceStates($cgid,$uid,$request_house,$request_room,$request_states,0);
    $statusCode = 200;

    return $response->withStatus($statusCode)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($dbResponse));
});
/*
 * change password
 */
$app->put("/m/changePassword",function (Request $request,Response $response, $args){
    $cgid = $this->jwt->userinfo->cgid;
    $request_body = $request->getParsedBody();
    $request_username=$request_body['username'];
    $request_old_pass=$request_body['old_pass'];
    $request_new_pass= $request_body['new_pass'];

    $dbResponse = changePassword($cgid,$request_username,$request_old_pass,$request_new_pass);
    $statusCode = 200;
    return $response->withStatus($statusCode)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($dbResponse));
});


/**
 * Arduino : Update switch Status of a device
 */
$app->put("/d/updateDevicesState",function (Request $request,Response $response, $args){
    $request_body = $request->getParsedBody();
    $cgid = $request_body['c'];
    $uid  = $request_body['u'];
    $request_house=  $request_body['h'];
    $request_room=$request_body['r'];
    $request_states=array($request_body['1'],$request_body['2'],$request_body['3'],$request_body['4']);
//$params = [$cgid,$uid,$request_house,$request_room,$request_states];
    $dbResponse = updateDeviceStates($cgid,$uid,$request_house,$request_room,$request_states,1);

    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($dbResponse));
});
/**
 * Mobile: Get List Of Devices With Status
 */
$app->get("/d/getListOfDevices",function (Request $request,Response $response, $args){
    $cgid = $request->getHeader('c')[0];
    $request_house=  $request->getHeader('h')[0];
    $request_room=$request->getHeader('r')[0];

    $dbResponse = listOfDevices($cgid,$request_house,$request_room);


    $resultSet= [$dbResponse["result"][0]["swtich_state"],$dbResponse["result"][1]["swtich_state"],$dbResponse["result"][2]["swtich_state"],$dbResponse["result"][3]["swtich_state"],$dbResponse["result"][0]["lastchangedfrom"]];
    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($resultSet));
});
/**
 *  Admin Site: HCS
 */
$app->post("/a/register",function (Request $request,Response $response, $args){
    $request_body = $request->getParsedBody();
    $request_name = $request_body['name'];
    $request_uname = $request_body['username'];
    $request_pass=$request_body['password'];
    $request_email = $request_body['email'];
    $responseObject = registerNewAdmin( $request_name,$request_email,$request_uname,$request_pass);
    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($responseObject));
});

$app->post("/a/login",function (Request $request,Response $response, $args){
    $request_body = $request->getParsedBody();
    $request_uname = $request_body['username'];
    $request_pass=$request_body['password'];
    $responseObject = authenticateAdminUser( $request_uname,$request_pass);
    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($responseObject));
});
$app->get("/a/getAdminUserList",function (Request $request,Response $response, $args){

    $dbResponse = getAdminUserList();
    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($dbResponse));
});

$app->get("/a/getClientGroupList",function (Request $request,Response $response, $args){

    $dbResponse = getClientGroupList();
    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($dbResponse));
});

$app->post("/a/addNewClient",function (Request $request,Response $response, $args){
    $request_body = $request->getParsedBody();
    $request_name = $request_body['name'];
    $request_uname = $request_body['username'];
    $request_pass=$request_body['password'];
    $request_email = $request_body['email'];
    $request_authcode = $request_body['authcode'];
    $responseObject = addNewClient( $request_name,$request_email,$request_uname,$request_pass,$request_authcode);
    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($responseObject));
});

$app->post("/a/deleteClient",function (Request $request,Response $response, $args){
    $request_body = $request->getParsedBody();
    $request_cgid = $request_body['cgid'];
    $responseObject = deleteClient( $request_cgid);
    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($responseObject));
});
$app->get("/a/getListOfHouses",function (Request $request,Response $response, $args){
     $cgid= $request->getQueryParams('cgid');
    $dbResponse = listOfHouses($cgid["cgid"]);
    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($dbResponse));
});

$app->post("/a/addHouse",function (Request $request,Response $response, $args){
    $request_body = $request->getParsedBody();
    $request_name = $request_body['name'];
    $request_details=$request_body['details'];
    $cgid=$request_body['cgid'];

    $dbResponse = addNewHouse($cgid,$request_name,$request_details);

    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($dbResponse));
});

$app->post("/a/addRoom",function (Request $request,Response $response, $args){
    $request_body = $request->getParsedBody();
    $cgid= $request_body['cgid'];
    $request_house= $request_body['houseid'];
    $request_name = $request_body['name'];
    $request_details=$request_body['details'];

    $dbResponse = addNewRoomWithDevices($cgid,$request_house,$request_name,$request_details);

    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($dbResponse));
});

$app->get("/a/getListOfRooms",function (Request $request,Response $response, $args){
    $cgid= $request->getQueryParams('cgid');
	$request_house= $request->getQueryParams('houseid');
    $dbResponse = listOfRooms($cgid['cgid'],$request_house['houseid']);
    return $response->withStatus(200)
        ->withHeader('Content-Type', 'application/json')
        ->write(json_encode($dbResponse));
});



////////////
////////////
$app->run();
////////////
////////////

?>