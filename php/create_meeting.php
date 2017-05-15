<?php

$response = array();
require_once __DIR__ . '/db_connect.php';
$db = new DB_CONNECT();
$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD,DB_DATABASE);

if (isset($_POST['date']) && isset($_POST['latitude']) && isset($_POST['longitude'])) {
 
//    $id = $_POST['id'];
    $date = $_POST['date'];
    $participants = $_POST['participants'];
    $time = $_POST['time'];
    $name = $_POST['name'];
    $address = $_POST['address'];
    $latitude = $_POST['latitude'];
    $longitude = $_POST['longitude'];
    $confirmcount = $_POST['confirmcount'];
    $organizer = $_POST['organizer'];
 
    $result = mysqli_query($con, "INSERT INTO meetings(date, participants, time, name, address, latitude, longitude, confirmcount, organizer) VALUES('$date', '$participants', '$time', '$name', '$address', '$latitude', '$longitude', '$confirmcount', '$organizer')");
 
    if ($result) {
        $response["success"] = 1;
        $response["message"] = "Meeting successfully created.";

        echo json_encode($response);
    } else {
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";

        echo json_encode($response);
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
    echo json_encode($response);
}
?>