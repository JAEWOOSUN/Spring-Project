<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<fmt:setTimeZone value="GMT+9" />
<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ page pageEncoding="utf-8" %>
<!doctype html>
<html>
<head>
    <%@include file="/WEB-INF/views/society/conference/onePage1/include/head.jsp" %>
    <title>${soConfConference.hostingInstitution} ${soConfConference.nameKor}</title>
    <style>
        ol, ul {
            padding-left: 0px;
        }

        #home-slider-2 > li img {
            margin-top: -20px;
        }

        .large{
            font-size: 1.2em;
        }

        .modal-body{
            max-height: 500px;
            overflow-y: auto;
        }

    </style>
</head>
<body id="body">
<div id="spinner">
    <img width="50%" src="<spring:eval expression="@config['resources.cdn']"/>/common/images/icons/loading.gif" alt="Loading..."/>
</div>
<div class="backtoTopDiv" style="position: fixed; bottom: 1.0em; right: 1.0em; z-index: 3000">
    <a href="#" class="backtotop"><img width="36" height="36" src="<spring:eval expression="@config['resources.cdn']"/>/society/images/arrows_2_3.png" title="Top" /></a>
</div>
<div id="wrapper">
    <%@include file="/WEB-INF/views/society/conference/onePage1/sections/banner_section/banner.jsp" %>
    <%@include file="/WEB-INF/views/society/conference/onePage1/include/navigation.jsp"%>

    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12 text-right">
            <br/>
            <a href="/society/${societyAbbr}/conference/${soConfConference.nameId}" style="margin-right: 1.6em !important;"
               class="btn btn-primary btn-sm">${soConfConference.nameId eq 'cute2020' || soConfConference.nameId eq 'NAT2020'? "Previous Page" :"이전 페이지로"}</a>
        </div>
    </div>
    <div style="text-align: center; padding:1em;">
        <input id="lotterySetting" type="checkbox" checked data-toggle="toggle" data-on="From Excel" data-off="From Zoom" data-onstyle="success" data-offstyle="primary">
    </div>
    <div class="container" id="excelSetting">
        <div class="row">
            <div class="col-md-1 col-sm-1 col-lg-1"></div>
            <div class="col-md-10 col-sm-10 col-lg-10">
                <h3><i class="fa fa-cloud-upload" aria-hidden="true"></i>&nbsp; Excel 참가자 업로드</h3>
                <form action="${baseURL}/society/${societyAbbr}/conference/${soConfConference.nameId}/prizeLottery/excelUpload" method="post" enctype="multipart/form-data">
                    <div style="float:left;display:inline-block;margin-left:2em;">
                        <input type="file" style="display:inline;" class="form-control-file btn btn-info" value="파일 선택" name="file"/>
                        <input type ="submit" class="btn btn-info" value="업로드"/>
                    </div>
                </form>
                <br/><br/><br/>
                <span style="margin-left:2em;"><i class="fa fa-info-circle" aria-hidden="true"></i>&nbsp;템플릿이 필요하신 분들은 <a href="https://manuscriptlink-society-file.s3-ap-northeast-1.amazonaws.com/prize_lottery/registrants_list.xlsx" target="_blank">여기</a>를 클릭해주세요. </span> <br/>
                <span style="margin-left:2em;"><i class="fa fa-info-circle" aria-hidden="true"></i>&nbsp;엑셀 파일 확장자는 통합 문서(.xlsx)파일로 저장해주세요. </span>
            </div>
            <div class="col-md-1 col-sm-1 col-lg-1"></div>
        </div>
        <br/><br/>
        <div class="row">
            <div class="col-md-1 col-sm-1 col-lg-1"></div>
            <div class="col-md-10 col-sm-10 col-lg-10" style="height:500px;overflow-y:scroll">
                <table class="table" id="prizeLotterySettingTableExcel">
                    <thead>
                    <tr>
                        <th scope="col">#</th>
                        <th scope="col">Name</th>
                        <th scope="col">Eamil</th>
                        <th scope="col">Phone Number</th>
                        <th scope="col">Prize Exclude</th>
                        <th scope="col">Already Prize</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${listExcelRegistrants}" var="curExcelRegistrants" varStatus="status">
                        <tr>
                            <td>${status.count}</td>
                            <td>${curExcelRegistrants.getName()}</td>
                            <td>${curExcelRegistrants.getEmail()}</td>
                            <td>${curExcelRegistrants.getPhone()}</td>
                            <td>${curExcelRegistrants.getPrizeExclude() eq 1 ? "<span style='color:green'>YES</span>" : "<span style='color:red'>NO</span>"}</td>
<%--                            <td>${curExcelRegistrants.getPrizeExclude()}</td>--%>
                            <td>${curExcelRegistrants.getAlreadyPrize() eq 1 ? "<span style='color:green'>YES</span>" : "<span style='color:red'>NO</span>"}</td>
<%--                            <td>${curExcelRegistrants.getAlreadyPrize()}</td>--%>
                    </c:forEach>
                    </tbody>
                </table>

            </div>
            <div class="col-md-1 col-sm-1 col-lg-1"></div>
        </div>
    </div>
    <div class="container" id="zoomSetting" style="display:none;">
        <div class="row" >
            <div class="col-md-1 col-sm-1 col-lg-1"></div>
            <div class="col-md-10 col-sm-10 col-lg-10">
                <h3><i class="fa fa-cloud-upload" aria-hidden="true"></i>&nbsp; Zoom 계정을 사용한 업로드</h3>
                <div class="dropdown">
                    <button id="dLabel" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" class="btn btn-primary" style="width:40%;font-size:1.5em; margin-left:1.7em;">
                        support@manuscriptlink.com
                    </button>
                    <br/><br/>
                    <div class="dropdown-menu scroll-menu scroll-menu-2x" aria-labelledby="dLabel" style="width:100%;overflow-y:scroll;height:500px;">
                        <div class="list-group" style="width:100%;">
                            <c:forEach items="${listWebinars}" var="curWebinars">
                                <a onclick="addTableRow('${curWebinars.id}', '${curWebinars.topic}', '${curWebinars.created_at}')" style="CURSOR:pointer;"  class="list-group-item list-group-item-action">
                                    <div class="d-flex w-100 justify-content-between">
                                        <h5 class="mb-1">${curWebinars.topic}</h5>
                                        <small>created_at : ${curWebinars.created_at}</small><br/>
                                        <small>start_time : ${curWebinars.start_time}</small>
                                    </div>
                                    <p class="mb-1">${curWebinars.agenda}</p>
                                    <small>join_url : ${curWebinars.join_url}</small>
                                </a>

                                <%--            <c:forEach items="${curWebinars}" var="map">--%>
                                <%--                ${map}<br/>--%>
                                <%--            </c:forEach>--%>

                            </c:forEach>
                        </div>
                    </div>
                </div>


            </div>
            <div class="col-md-1 col-sm-1 col-lg-1"></div>
        </div>
        <div class="row">
            <div class="col-md-1 col-sm-1 col-lg-1"></div>
            <div class="col-md-10 col-sm-10 col-lg-10">

                <table class="table" id="prizeLotterySettingTable">
                    <thead>
                    <tr>
                        <th scope="col">#</th>
                        <th scope="col">Zoom ID</th>
                        <th scope="col">TOPIC</th>
                        <th scope="col">created_at</th>
                        <th scope="col">Close</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>

            </div>
            <div class="col-md-1 col-sm-1 col-lg-1"></div>
        </div>
        <div class="row">
            <div class="col-md-1 col-sm-1 col-lg-1"></div>
            <div class="col-md-10 col-sm-10 col-lg-10">
                <a class="btn btn-info confirm-registrants" style="float:right;">참가자 등록하기</a>
            </div>
            <div class="col-md-1 col-sm-1 col-lg-1"></div>
        </div>
    </div>
    <div>

<%--        <form:form modelAttribute="chooseZoomMail">--%>
<%--            <form:checkbox path="chooseZoomMail" value="001" label="A" cssClass="ck_box"/>--%>
<%--            <form:checkbox path="chooseZoomMail" value="002" label="B" cssClass="ck_box"/>--%>
<%--            <form:checkbox path="chooseZoomMail" value="003" label="C" cssClass="ck_box"/>--%>
<%--            <input type="submit" value="선택"/>--%>
<%--        </form:form>--%>
    </div>

    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12 text-right">
            <br/>
            <a href="/society/${societyAbbr}/conference/${soConfConference.nameId}" style="margin-right: 1.6em !important;"
               class="btn btn-primary btn-sm">${soConfConference.nameId eq 'cute2020' || soConfConference.nameId eq 'NAT2020'? "Previous Page" :"이전 페이지로"}</a>
        </div>
    </div>
    <br/>
    <%@include file="/WEB-INF/views/society/conference/onePage1/sections/location_section.jsp" %>
    <%@include file="/WEB-INF/views/society/conference/onePage1/sections/footer_section.jsp" %>
</div>
<%@include file="/WEB-INF/views/society/conference/onePage1/include/foot.jsp" %>
<script>

    $(function () {
        $('.dropdown-toggle').dropdown()
    })

    <%--function chooseZoomRoom() {--%>
    <%--    $.ajax({--%>
    <%--        type: "GET",--%>
    <%--        url: '/society/' + ${societyAbbr} + '/conference/' + ${soConfConference.nameId} + '/prizeLottery/setting/zoomRoom/ajax',--%>
    <%--        success: function(data) {--%>
    <%--            $('#notice-information-section-div').html(data);--%>
    <%--        }--%>
    <%--    });--%>
    <%--}--%>

    let tableRowList = [];
    async function addTableRow(num, topic, created_at){

        let check = true;
        await $.each(tableRowList, function(idx){
            if(tableRowList[idx].num == num) {
                alert('중복되었습니다.');
                check = false;
                return;
            }
        });

        if(check){
            $('#prizeLotterySettingTable > tbody:last').append('<tr>\n' +
                '                    <th scope="row">'+'</th>\n' +
                '                    <td>'+num+'</td>\n' +
                '                    <td>'+topic+'</td>\n' +
                '                    <td>'+created_at+'</td>\n' +
                '                    <td style="float:left;"><button type="button" class="close delete_row" aria-label="Close"> <span aria-hidden="true">&times;</span> </button></td>\n' +
                '                </tr>')
            tableRowList.push({'num':num, 'topic':topic, 'created_at':created_at});
            console.log(tableRowList);

            getZoomRegistraints(num);
        }
    }

    function getZoomRegistraints(num){
        $.ajax({
            url:'/society/${societyAbbr}/conference/${nameId}/prizeLottery/setting/zoomRoom/ajax?zoomId='+num,
            success:function(data){
                var registrantsList = "";
                $.each(data, function(idx, value){
                    registrantsList += ('<tr>' +
                        '<td>'+idx+'</td>' +
                        '<td>'+value.first_name+value.last_name+'</td>' +
                        '<td>'+value.email+'</td>' +
                        '</tr>');
                });

                var table_head = '<table class="table">\n'
                    + '<thead>\n'
                    + '<tr>\n'
                    + '<th scope="col">#</th>\n'
                    + '<th scope="col">Name</th>\n'
                    + '<th scope="col">Email</th>\n'
                    + '</tr>\n'
                    + '</thead>\n'
                    + '<tbody>\n';

                var table_foot = '</tbody>\n' +
                    '                </table>';

                bootbox.alert({
                    title: "참가자 목록",
                    message: table_head+registrantsList+table_foot,
                    // buttons: {
                    //     confirm: {
                    //         label: '<i class="fa fa-check"></i> Ok'
                    //     }
                    // }
                });
            },
            error: function (jqXHR, textStatus, errorThrown){
                console.log("error");
            }
        });
    }

    $(function() {
        $('#lotterySetting').change(function () {
            if($('#zoomSetting').css('display') == 'none'){
                $('#excelSetting').css('display', 'none');
                $('#zoomSetting').css('display', '');
            }else{
                $('#excelSetting').css('display', '');
                $('#zoomSetting').css('display', 'none');
            }
            // $('#zoomSetting').css('display', 'none');
        });
    })

    //Delete the row from the table and delete it from the tablelist.
    $(document).on('click', 'button.delete_row', function (){
        var deleteNum = $(this).parent().parent().children().eq(1).text();
        $(this).parent().parent().remove();

        const findidx = tableRowList.findIndex(function(item){return item.num === deleteNum});
        if(findidx > -1) tableRowList.splice(findidx, 1);

        console.log(tableRowList);

    });

    //Add a list of participants to the appropriate online conference.
    $(document).on('click', 'a.confirm-registrants', function (){
        if(tableRowList.length ==0){
            alert('Check your Participant List to add.')
            return;
        }
        bootbox.confirm({
            title: "<span style='color:red;font-weight:bold'><i class='fa fa-check'></i>&nbsp;Warning</span>",
            message: "<span style='color:red;'>${soConfConference.nameKor}</span>에 참가자 목록이 등록됩니다.<br/><br/>계속하시겠습니까?",
            buttons: {
                cancel: {
                    label: '<i class="fa fa-times"></i> Cancel'
                },
                confirm: {
                    label: '<i class="fa fa-check"></i> Confirm'
                }
            },
            callback: function (result) {
                if(result){
                    $.ajax({
                        url:'${baseUrl}/society/${societyAbbr}/conference/${soConfConference.nameId}/prizeLottery/setting/zoomRoom/ajax/submit',
                        type:"POST",
                        dataType:"json",
                        contentType:"application/x-www-form-urlencoded; charset=UTF-8",
                        data: {
                            'zoomRoomList': JSON.stringify(tableRowList)
                        },
                        success : function(data){
                            if(data){
                                alert("등록되었습니다.");
                                location.reload();
                            }else{
                                alert("실패했습니다.");
                                location.reload();
                            }
                            tableRowList = [];
                        },
                        complete : function(){
                            tableRowList = [];
                        }
                    })
                }
            }
        });
    })


    $(document).ready(function(){
        console.log();

    });
</script>
<link href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css" rel="stylesheet">
<script src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>
</body>
</html>