<?php
$response = array();
require_once __DIR__ . '/db_connect.php';
$db = new DB_CONNECT();
$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD,DB_DATABASE);



if (isset($_POST['name']) ) {
 
    $name = $_POST['name'];
    //$description = $_POST['description'];
 	//$date = $_POST['date'];
// 	$time = $_POST['time'];
 //	$meeting = $_POST['meeting'];
 	$latitude = $_POST['latitude'];
 	$longitude = $_POST['longitude'];
 	$address = $_POST['address'];
// 	$hasconfirmed = $_POST['hasconfirmed'];
 
	$result = mysqli_query($con, "INSERT INTO users(name, latitude, longitude, address) VALUES('$name', '$latitude', '$longitude', '$address')") or die(mysqli_error());

   if ($result) {
        $response["success"] = 1;
        $response["message"] = "User successfully created.";
 
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = mysqli_error($con);

        echo "Error: " . $sql . "<br>" . mysqli_error($con);    
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    echo json_encode($response);
}
?>