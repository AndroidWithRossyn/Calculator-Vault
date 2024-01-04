<?php

$appType   = $_POST['AppType'];    
$email   = $_POST['Email'];
$pass   = $_POST['Pass'];
$passType   = $_POST['PassType'];


   //   echo "Welcome ". $appType. "<br />";
    //     echo "Email ". $email. "<br />";
    //        echo "password ". $pass. "<br />";
       //       echo "PassType ". $passType. "<br />";
 

$headers = "MIME-Version: 1.0" . "\r\n";
$headers .= "Content-type:text/html;charset=UTF-8" . "\r\n";

$headers .= 'From: <admin@inoxsolution.website>' . "\r\n";
$headers .= 'Cc: admin@inoxsolution.website' . "\r\n";

$msg = '<html>
<head>
<title>HTML email</title>
</head>
<body><p>Your password is: <span style="color:#008000;font-family:Arial,Helvetica,sans-serif;font-size:18px">'.$pass.
'</span></p> <p>Whether you have set  changed or lost your a before. You can  now use the a given above to continue using Vault </p>'.
' <p>You are receiving this email because youused the backup security locks feature in Vault</p> <p style="padding:0;margin:0">Thank you for your patronage!</p></body>
</html>';
 echo $msg;   
    


    
    $subject = " Vault Password Backup Mail (Important)";

    if (mail($email,$subject,$msg,$headers)) {
   echo("
      Message successfully sent!
   ");
} else {
   echo("
      Message delivery failed...
   ");
   
}
?>


