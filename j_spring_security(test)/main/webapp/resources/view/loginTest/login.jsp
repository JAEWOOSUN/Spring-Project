<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>LOGIN PAGE</title>

    <c:set var="resources"><spring:eval expression="@config['resources.cdn']" /></c:set>
    <c:set var="baseUrl" value="${req.scheme}://${req.serverName}:${req.serverPort}${req.contextPath}" />

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="//fonts.googleapis.com/css?family=Open+Sans" />
    <link href="${resources}/vendor/metronic_assets_4.5/global/plugins/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css" />

    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>

    <style>
        body {font-family: Arial, Helvetica, sans-serif;}
        form {border: 3px solid #f1f1f1;}

        input[type=text], input[type=password] {
            width: 80%;
            padding: 12px 20px;
            margin: 8px 0;
            display: inline-block;
            border: 1px solid #ccc;
            box-sizing: border-box;
            margin:0.5em;
        }

        button {
            background-color: #4CAF50;
            color: white;
            padding: 14px 20px;
            margin: 8px 0;
            border: none;
            cursor: pointer;
            width: 80%;
        }

        div.box{
            text-align: center;
        }

        button:hover {
            opacity: 0.8;
        }

        .cancelbtn {
            width: auto;
            padding: 10px 18px;
            background-color: #f44336;
        }

        .imgcontainer {
            text-align: center;
            margin: 24px 0 12px 0;
        }

        img.avatar {
            width: 40%;
            border-radius: 50%;
        }

        .container {
            padding: 16px;
        }

        /* Change styles for span and cancel button on extra small screens */
        @media screen and (max-width: 300px) {
            span.psw {
                display: block;
                float: none;
            }
            .cancelbtn {
                width: 100%;
            }
        }

        .google-btn {
            width: 42px;
            height: 42px;
            background-color: #4285f4;
            border-radius: 2px;
            box-shadow: 0 3px 4px 0 rgba(0, 0, 0, .25);
            cursor:pointer;
            display:inline-block;
        }
        .google-icon-wrapper {
            position: absolute;
            margin-top: 1px;
            margin-left: 1px;
            width: 40px;
            height: 40px;
            border-radius: 2px;
            background-color: #fff;
        }
        .google-icon {
            position: absolute;
            margin-top: 11px;
            margin-left: -8px;
            width: 18px;
            height: 18px;
        }

        .google-btn:hover {
             box-shadow: 0 0 6px #4285f4;
         }
        .google-btn:active {
             background: #1669F2;
         }

    </style>
</head>
<body>

<div class="container">
    <div class="row">
        <div class="col-md-2 col-sm-2"></div>
        <div class="col-md-8 col-sm-8">
            <br/><br/>
            <h2 style="text-align: center;font-weight:bold;">Login Test</h2><br/>
            <div id="spring-security-alert-danger" class="alert alert-danger alert-dismissible display-hide" style="width:100%;">
                <button type="button" class="btn close" data-dismiss="alert" aria-label="Close" style="text-align:right;top:-7px;">
                    <span aria-hidden="true">&times;</span>
                </button>
                <span><spring:message code="loginTest.checkAgainIdPw"/></span>
            </div>
            <form class="login-form" action="j_spring_security_check" method="post">

                <div style="text-align: center;">
                    <label for="uname" style="padding-top:0.5em;"><b>User ID</b></label><br/>
                    <input type="text" name="j_username" autocomplete="off" placeholder="Enter ID" required><br/>

                    <label for="psw"><b>Password</b></label><br/>
                    <input type="password" name="j_password" autocomplete="off" placeholder="Enter Password" required><br/>

                    <button type="submit">Login</button><br/>
                    <div class="box">
                        <p style="text-align: right;width:90%"><input type="checkbox" checked="checked" name="remember"> Remember me</p>
                        <p ><i class="fa fa-info-circle fa-lg" aria-hidden="true"></i>&nbsp;Forgot <a href="#">password?</a></p>
                        <p ><i class="fa fa-info-circle fa-lg" aria-hidden="true"></i>&nbsp; Not Signed up yet?&nbsp;<a href="${baseURL}/loginTest/registration">Signup</a></p>
                        <p><i class="fa fa-info-circle fa-lg" aria-hidden="true"></i>&nbsp;Login with Google </p>
                        <div class="google-btn">
                            <div class="google-icon-wrapper">
                                <img class="google-icon" src="https://upload.wikimedia.org/wikipedia/commons/5/53/Google_%22G%22_Logo.svg"/>
                            </div>
                        </div>
                        <br/>
                    </div>
                </div>

            </form>

        </div>
        <div class="col-md-2 col-sm-2"></div>
    </div>
</div>


</body>
<script>
    var Login = function() {
        var handleLogin = function() {
            $('.login-form').validate({
                rules: {
                    j_username: {
                        required: true
                    },
                    j_password: {
                        required: true
                    },
                    remember: {
                        required: false
                    }
                },

                messages: {
                    username: {
                        required: "Username is required."
                    },
                    password: {
                        required: "Password is required."
                    }
                },

                // invalidHandler: function (event, validator) { //display error alert on form submit
                //     $('#login-form-alert-danger', $('.login-form')).show();
                // },

                submitHandler: function (form) {
                    form.submit(); // form validation success, call ajax form submit
                }
            });
        }
        return {
            //main function to initiate the module
            init: function() {
                handleLogin();
            }
        };
    }();


    jQuery(document).ready(function() {
        //Login.init();
        var login_error = '${msg}';
        console.log(login_error);
        if (login_error.length > 0) {
            $('#spring-security-alert-danger').show();
            console.log("show");
        } else {
            $('#spring-security-alert-danger').hide();
            console.log("hide");
        }

    });

    $('.google-btn').click(function(){
        var win;
        var checkConnect;

        win = window.open('https://accounts.google.com/o/oauth2/v2/auth?client_id=806684006630-c5qu9q0t9hnfe6q0rodclmsuee4dtjpl.apps.googleusercontent.com&redirect_uri=http://localhost:8080/loginTest/google-redirect&response_type=code&scope=email%20profile%20openid&access_type=offline',
        "Google Login", "width:400");

        checkConnect = setInterval(function(){
            if(!win || !win.closed) return;
            clearInterval(checkConnect);
            location.href = "${baseURL}/loginTest/loginResult";
        }, 100);

    })
</script>
</html>
