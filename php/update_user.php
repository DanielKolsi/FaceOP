<?php
 
$response = array();
require_once __DIR__ . '/db_connect.php';
$db = new DB_CONNECT();
$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD,DB_DATABASE);


if (isset($_POST['name'])) {
 
// 	$id = $_POST['id'];
 	$name = $_POST['name'];
    $description = $_POST['description'];
 	$date = $_POST['date'];
 	$time = $_POST['time'];
 	$meetings = $_POST['meetings'];
 	$latitude = $_POST['latitude'];
 	$longitude = $_POST['longitude'];
 	$address = $_POST['address'];
 	$hasconfirmed = $_POST['hasconfirmed'];

 
	 $result = mysqli_query($con, "UPDATE users SET description = '$description', meetings = '$meetings', date = '$date', time = '$time', latitude = '$latitude', longitude = '$longitude', address = '$address', hasconfirmed = '$hasconfirmed' WHERE name = '$name'")  or die(mysqli_error());

 
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "User successfully updated.";

        echo json_encode($response);
    } else {
 		$response["success"] = 0;
        $response["message"] = "User NOT updated.";
        echo json_encode($response); 
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    echo json_encode($response);
}
?>