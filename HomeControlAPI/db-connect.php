<?php

$host = "127.0.0.1";
$port = "3306";
$dbname = "smart_home_control";
$user = "root";
$password = "";

$connectionString = "mysql:host=$host;port=$port;dbname=$dbname";
$options = array(
	PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
	PDO::ATTR_EMULATE_PREPARES => false
);

$db_connection = new PDO($connectionString, $user, $password,$options);
?>
