<?php

// array for JSON response
$response = array();
require_once __DIR__ . '/db_connect.php';
$db = new DB_CONNECT();
$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD,DB_DATABASE);

// check for post data
if (isset($_GET["name"])) {
   $name = $_GET['name'];

    $result = mysqli_query($con, "SELECT * FROM users WHERE name = '$name'");
 
    if (!empty($result)) {
        // check for empty result
        if (mysqli_num_rows($result) > 0) {
 
            $result = mysqli_fetch_array($result);
 
            $user = array();
            $user["id"] = $result["id"];
            $user["name"] = $result["name"];
            $user["description"] = $result["description"];
            $user["date"] = $result["date"];
            $user["time"] = $result["time"];
            $user["latitude"] = $result["latitude"];
            $user["longitude"] = $result["longitude"];
            $user["address"] = $result["address"];
            $user["meetings"] = $result["meetings"];
            $user["hasconfirmed"] = $result["hasconfirmed"];
            // success
            $response["success"] = 1;
 
            // user node
            $response["user"] = array();
 
            array_push($response["user"], $user);
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no user found
            $response["success"] = 0;
            $response["message"] = "Noooo user found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no user found
        $response["success"] = 0;
        $response["message"] = "NoPE user found";
 
        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>