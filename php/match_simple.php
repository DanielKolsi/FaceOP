<?php
$response = array();
require_once __DIR__ . '/db_connect.php';
$db = new DB_CONNECT();
$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD,DB_DATABASE);

$time = $_GET['time'];
$date = $_GET['date'];
$address = $_GET['address'];

$result = mysqli_query($con, "select * from users where time = '$time' and date = '$date' and address = '$address'") or die(mysqli_error());        

if (mysqli_num_rows($result) > 0) {

    $response["users"] = array();
 
    while ($row = mysqli_fetch_array($result)) {

       	$user = array();
		$user["name"] = $row["name"];
    	array_push($response["users"], $user);
    }
    // success
    $response["success"] = 1;
    echo json_encode($response);
} else {
    $response["success"] = 0;
    $response["message"] = "No users found";
 
    echo json_encode($response);
}
?>