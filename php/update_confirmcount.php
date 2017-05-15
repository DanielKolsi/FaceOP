<?php
 
$response = array();
require_once __DIR__ . '/db_connect.php';
$db = new DB_CONNECT();
$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD,DB_DATABASE);

if (isset($_POST['m_name'])) {
 
// 	$id = $_POST['id'];
 	$name = $_POST['m_name'];
    $confirmcount = $_POST['confirmcount'];
 	$result = mysqli_query($con, "UPDATE meetings SET confirmcount = '$confirmcount' WHERE name = '$name'")  or die(mysqli_error());
 
    if ($result) {
        $response["success"] = 1;
        $response["message"] = "User successfully updated.";
        echo json_encode($response);
    } 
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
    echo json_encode($response);
}
?>