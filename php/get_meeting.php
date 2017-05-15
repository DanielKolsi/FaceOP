<?php

$response = array();
require_once __DIR__ . '/db_connect.php';
$db = new DB_CONNECT();
$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD,DB_DATABASE);

if (isset($_GET["name"])) {
   $name = $_GET['name'];
   $result = mysqli_query($con, "SELECT * FROM meetings WHERE participants LIKE CONCAT('%', '$name', '%')");
 
    if (!empty($result)) {
        if (mysqli_num_rows($result) > 0) {
        
            $result = mysqli_fetch_array($result);
            $meeting = array();
 //           $meeting["id"] = $result["id"];
            $meeting["m_name"] = $result["name"];
            $meeting["participants"] = $result["participants"];
            $meeting["m_latitude"] = $result["latitude"];
            $meeting["m_longitude"] = $result["longitude"];
            $meeting["m_date"] = $result["date"];
            $meeting["m_time"] = $result["time"];
            $meeting["m_address"] = $result["address"];
            $meeting["confirmcount"] = $result["confirmcount"];
            $meeting["organizer"] = $result["organizer"];
            
            $response["success"] = 1;
            $response["meetings"] = array();

            array_push($response["meetings"], $meeting);
            echo json_encode($response);
        } else {
            $response["success"] = 0;
            $response["message"] = "No meeting found.";
            echo json_encode($response);
        }
    } else {
        $response["success"] = 0;
        $response["message"] = "No meeting found; empty result.";
        echo json_encode($response);
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
    echo json_encode($response);
}
?>