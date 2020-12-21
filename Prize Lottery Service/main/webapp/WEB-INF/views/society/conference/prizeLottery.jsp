<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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

        /* Global styles come from external css https://codepen.io/GreenSock/pen/JGaKdQ*/
        .gift{
            cursor: pointer;
        }
        .box {
            margin:1em;
            cursor: pointer;
            width:100px;
            height:100px;
            vertical-align: middle;
        }

        .box p{
            text-align: center;
            font-size:3em;
            color:white;
        }

        .prize_box{
            margin:1em;
            cursor: pointer;
            width:300px;
            height:300px;
        }

        .pink {background-color: pink;}
        .orange {background-color: orange;}
        .grey {background-color: lightgrey;}
        .green {background-color: mediumseagreen;}

        .modal-body {
            height: 500px;
            overflow-y: scroll;
        }

        .already_prize_yes{
            color:green;
            cursor:pointer;
        }
        .already_prize_no{
            color:red;
            cursor:pointer;
        }
        .prize_exclude_yes{
            color:green;
            cursor:pointer;
        }
        .prize_exclude_no{
            color:red;
            cursor:pointer;
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
    <div class="container">
        <div class="row">
            <div class="col-md-3 col-sm-3">
                <h2 class="subpageTitle">경품 추첨</h2>
            </div>
            <div class="col-md-9 col-sm-9">
            </div>
        </div>
        <br/>
    </div>

    <div class="prizeLotteryAjax"></div>
    <div class="container">
        <div class="row">
            <div class="col-md-1 col-sm-1 col-lg-1"></div>
            <div class="col-md-10 col-sm-10 col-lg-10">
                <div style="margin:1em;margin-top:3em;float:right;">
                    <a class="btn btn-info registrants_list" >참가자 목록</a>&nbsp;&nbsp;<a class="btn btn-info already_registrants" >당첨된 참가자</a>&nbsp;&nbsp;<a class="btn btn-danger make_init" >참가자 목록 초기화하기</a>
                </div>
            </div>
            <div class="col-md-1 col-sm-1 col-lg-1"></div>
        </div>
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
</body>
<script>

    var prize_exclude_set = new Set();

    $(document).ready(function(){
        $.ajax({
            url: '${baseUrl}/society/${societyAbbr}/conference/${soConfConference.nameId}/prizeLottery/ajax',
            success:function(data){
                $('.prizeLotteryAjax').html(data);
            }
        })

    });

    $('.make_init').click(function(){
        $.ajax({
            url:'${baseUrl}/society/${societyAbbr}/conference/${soConfConference.nameId}/prizeLottery/ajax?init=on',
            success:function(data){
                alert('참가자 목록이 초기화되었습니다.');
                $('.prizeLotteryAjax').html(data);
            }
        })
    });
    $('.registrants_list').click(function(){

        var table_head = '<table class="table">\n'
            + '<thead>\n'
            + '<tr>\n'
            + '<th scope="col">#</th>\n'
            + '<th scope="col">Name</th>\n'
            + '<th scope="col">Email</th>\n'
            + '<th scope="col">Phone Number</th>\n'
            + '<th scope="col"><span style="color:red;font-size:0.5em;">*modifiable</span><br/>Prize Exclude</th>\n'
            + '<th scope="col">Already Prize</th>\n'
            + '</tr>\n'
            + '</thead>\n'
            + '<tbody>\n';

        var table_foot = '</tbody>\n' +
            '                </table>';

        $.ajax({
            url:'${baseUrl}/society/${societyAbbr}/conference/${soConfConference.nameId}/prizeLottery/table?list=all',
            success:function(data){
                bootbox.confirm({
                    title: "참석자 목록",
                    message: table_head+data+table_foot,
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
                                url:'${baseUrl}/society/${societyAbbr}/conference/${soConfConference.nameId}/prizeLottery/ajax',
                                type:"POST",
                                dataType:"json",
                                contentType:"application/x-www-form-urlencoded; charset=UTF-8",
                                data: {
                                    'prizeExclude': Array.from(prize_exclude_set)
                                },
                                // dataType:"json",
                                // contentType : "application/json;charset=UTF-8",
                                success : function(data){
                                    $('.prizeLotteryAjax').html(data);
                                    prize_exclude_set.clear();
                                },
                                complete : function(){
                                    prize_exclude_set.clear();
                                }
                            })
                        }
                    }
                });
            }
        })
    });
    $('.already_registrants').click(function(){

        var table_head = '<table class="table">\n'
            + '<thead>\n'
            + '<tr>\n'
            + '<th scope="col">#</th>\n'
            + '<th scope="col">Name</th>\n'
            + '<th scope="col">Email</th>\n'
            + '<th scope="col">Phone Number</th>\n'
            + '<th scope="col">Prize Exclude</th>\n'
            + '<th scope="col">Already Prize</th>\n'
            + '</tr>\n'
            + '</thead>\n'
            + '<tbody>\n';

        var table_foot = '</tbody>\n' +
            '                </table>';

        $.ajax({
            url:'${baseUrl}/society/${societyAbbr}/conference/${soConfConference.nameId}/prizeLottery/table?list=already',
            success:function(data){
                bootbox.alert({
                    title: "당첨된 참가자",
                    message: table_head+data+table_foot,
                    // buttons: {
                    //     confirm: {
                    //         label: '<i class="fa fa-check"></i> Ok'
                    //     }
                    // }
                });
            }
        })
    });

    $(document).on('click', '.prize_exclude_no', function(){
        $(this).removeClass('prize_exclude_no');
        $(this).text("YES");
        $(this).addClass('prize_exclude_yes');
        setToggle(prize_exclude_set, $(this).parent().parent().find('.tempId').text());
        console.log( prize_exclude_set);
    });
    $(document).on('click', '.prize_exclude_yes', function(){
        $(this).removeClass('prize_exclude_yes');
        $(this).text("NO");
        $(this).addClass('prize_exclude_no');
        setToggle(prize_exclude_set, $(this).parent().parent().find('.tempId').text());
        console.log( prize_exclude_set);
    });

    function setToggle(tempSet, num){
        if(tempSet.has(num)){
            tempSet.delete(num);
        }
        else{
            tempSet.add(num);
        }
    }

</script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/gsap/3.5.1/gsap.min.js"></script>
</html>