<?php

// array for JSON response
$response = array();
require_once __DIR__ . '/db_connect.php';
$db = new DB_CONNECT();
$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD,DB_DATABASE);

// check for post data
if (isset($_GET["name"])) {
   $name = $_GET['name'];

    $result = mysqli_query($con, "SELECT hasconfirmed FROM users WHERE name = '$name'");
 
    if (!empty($result)) {
        // check for empty result
        if (mysqli_num_rows($result) > 0) {
 
            $result = mysqli_fetch_array($result);
 
            $user = array();
            $user["hasconfirmed"] = $result["hasconfirmed"];

            $response["success"] = 1;
            $response["user"] = array();
            array_push($response["user"], $user);
            echo json_encode($response);
        } else {
            $response["success"] = 0;
            $response["message"] = "No hasconfirmed found.";
            echo json_encode($response);
        }
    } else {
        $response["success"] = 0;
        $response["message"] = "No hasconfirmed found; empty result.";
        echo json_encode($response);
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    echo json_encode($response);
}
?>