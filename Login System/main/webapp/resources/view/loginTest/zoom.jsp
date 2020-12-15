<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
    <title>Zoom</title>
    <!-- import #zmmtg-root css -->
    <link type="text/css" rel="stylesheet" href="https://source.zoom.us/1.8.1/css/bootstrap.css" />
    <link type="text/css" rel="stylesheet" href="https://source.zoom.us/1.8.1/css/react-select.css" />
    <c:set var="curURL" value="${pageContext.request.requestURL}" />
    <c:set var="req" value="${pageContext.request}" />
    <c:set var="baseUrl" value="${req.scheme}://${req.serverName}:${req.serverPort}${req.contextPath}" />
</head>
<div class="container">
    <div class="col-md-1 col-sm-1"></div>
    <div class="col-md-10 col-sm-10">
        <body class="ReactModal__Body--open">

        <!-- added on import -->
        <div id="zmmtg-root"></div>
        <div id="aria-notify-area"></div>

        <!-- added on meeting init -->
        <div class="ReactModalPortal"></div>
        <div class="ReactModalPortal"></div>
        <div class="ReactModalPortal"></div>
        <div class="ReactModalPortal"></div>
        <div class="global-pop-up-box"></div>
        <div class="sharer-controlbar-container sharer-controlbar-container--hidden"></div>

        <!-- import ZoomMtg dependencies -->
        <script src="https://source.zoom.us/1.8.1/lib/vendor/react.min.js"></script>
        <script src="https://source.zoom.us/1.8.1/lib/vendor/react-dom.min.js"></script>
        <script src="https://source.zoom.us/1.8.1/lib/vendor/redux.min.js"></script>
        <script src="https://source.zoom.us/1.8.1/lib/vendor/redux-thunk.min.js"></script>
        <script src="https://source.zoom.us/1.8.1/lib/vendor/jquery.min.js"></script>
        <script src="https://source.zoom.us/1.8.1/lib/vendor/lodash.min.js"></script>

        <!-- import ZoomMtg -->
        <script src="https://source.zoom.us/zoom-meeting-1.8.1.min.js"></script>

        <!-- import local .js file -->
        <%--<script src="js/index.js"></script>--%>
        </body>

    </div>
    <div class="col-md-1 col-sm-1"></div>


</div>

<script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>
<script>

    const meetingConfig = {
        apiKey:'DLDlXtjFQTOCzjzqaIjLuA',
        meetingNumber: '94057087789',
        leaveUrl: 'http://localhost:8080',
        userName: 'jaewoosun',
        userEmail: 'jaewoo@naver.com', // required
        // passWord: 'password', // if required
        role: 0 // 1 for host; 0 for attendee
    };

    const zoomMeeting = document.getElementById("zmmtg-root")


    function getSignature(meetConfig) {
        // make a request for a signature
        //how about use baseURL
        fetch("${baseUrl}/apiTest/zoom/dashboard/generateSignature", {
            method: 'POST',
            body: JSON.stringify(meetConfig)
        })
            .then(result => result.text())
            .then(response => {
                // call the init method with meeting settings
                ZoomMtg.init({
                    leaveUrl: meetConfig.leaveUrl,
                    isSupportAV: true,
                    // on success, call the join method
                    success: function() {
                        ZoomMtg.join({
                            // pass your signature response in the join method
                            signature: response,
                            apiKey: meetConfig.apiKey,
                            meetingNumber: meetConfig.meetingNumber,
                            userName: meetConfig.userName,
                            passWord: meetConfig.passWord,
                            userEmail: meetConfig.userEmail,
                            // on success, get the attendee list and verify the current user
                            success: function (res) {
                                console.log("join meeting success");
                                console.log("get attendee list");
                                ZoomMtg.getAttendeeslist({});
                                ZoomMtg.getCurrentUser({
                                    success: function (res) {
                                        console.log("success getCurrentUser", res.result.currentUser);
                                    },
                                });
                            },
                            error: function (res) {
                                console.log(res);
                            },
                        })
                    }
                })
            })
    }

    $(document).ready(function () {
        // For CDN version default
        ZoomMtg.setZoomJSLib('https://dmogdx0jrul3u.cloudfront.net/1.8.1/lib', '/av');
        ZoomMtg.preLoadWasm();
        ZoomMtg.prepareJssdk();

        getSignature(meetingConfig);
        console.log('${baseUrl}');
    });


</script>
</html>
