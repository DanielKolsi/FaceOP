<?php
 $response = array();
require_once __DIR__ . '/db_connect.php';
$db = new DB_CONNECT();
$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);

if (isset($_POST['m_name'])) {
    $name = $_POST['m_name'];
 
    require_once __DIR__ . '/db_connect.php';
 
    $db = new DB_CONNECT();
    $result = mysqli_query($con, "DELETE FROM meetings WHERE name = '$name'") or die(mysqli_error());
 
    if (mysqli_affected_rows() > 0) {
        $response["success"] = 1;
        $response["message"] = "Meeting successfully deleted!";
        echo json_encode($response);
    } else {
        $response["success"] = 0;
        $response["message"] = "No meeting found!";
        echo json_encode($response);
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing!";
    echo json_encode($response);
}
?>