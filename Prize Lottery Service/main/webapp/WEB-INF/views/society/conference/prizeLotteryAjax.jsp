<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container">
    <div class="row">
        <div class="col-md-1 col-sm-1 col-lg-1"></div>
        <div class="col-md-10 col-sm-10 col-lg-10">
            <br/>
            <div class="gift" style="text-align: center;">
                <img src="<spring:eval expression="@config['resources.cdn']"/>/society/images/gift_2.png" width="70%">
            </div>
            <div class="boxes" style="display:none;">
                <div id="output" style="text-align: center;padding:0.5em;font-size:1.4em;font-width: bold;font-family: 'Nanum Gothic';"></div>
                <div class="box green"><p>1</p></div>
                <div class="box orange"><p>2</p></div>
                <div class="box grey"><p>3</p></div>
                <div class="box pink"><p>4</p></div>
                <div class="box green"><p>5</p></div>
                <div class="box orange "><p>6</p></div>
                <div class="box grey "><p>7</p></div>
                <div class="box pink "><p>8</p></div>
                <div class="box green "><p>9</p></div>
                <div class="box orange "><p>10</p></div>
                <div class="box grey "><p>11</p></div>
                <div class="box pink"><p>12</p></div>
                <div class="box green"><p>13</p></div>
                <div class="box orange"><p>14</p></div>
                <div class="box grey"><p>15</p></div>
                <div class="box pink"><p>16</p></div>
                <div class="box green"><p>17</p></div>
                <div class="box orange"><p>18</p></div>
                <div class="box grey"><p>19</p></div>
                <div class="box pink"><p>20</p></div>
                <div class="box green"><p>21</p></div>
            </div>
            <div id="fireworks" style="float:left;padding:1em;">
                <div class="circle blue">
                    <div class="spot"></div>
                    <div class="inner circleInner1"></div>
                    <div class="inner circleInner2"></div>
                    <div class="inner circleInner3"></div>
                    <div class="inner circleInner4"></div>
                </div>
                <div class="circle red">
                    <div class="spot"></div>
                    <div class="inner circleInner1"></div>
                    <div class="inner circleInner2"></div>
                    <div class="inner circleInner3"></div>
                    <div class="inner circleInner4"></div>
                </div>
                <div class="circle green1">
                    <div class="spot"></div>
                    <div class="inner circleInner1"></div>
                    <div class="inner circleInner2"></div>
                    <div class="inner circleInner3"></div>
                    <div class="inner circleInner4"></div>
                </div>
            </div>
            <div class="prize_box_wrap" style="display:none;text-align:center;">
                <div class="prize_box" style="display:inline-block;"><p></p></div>
            </div>

        </div>
        <div class="col-md-1 col-sm-1 col-lg-1"></div>
    </div>
</div>
<script>

    $(document).ready(function(){
        gsap.from(".gift", {
            duration: 2,
            scale: 0.5,
            opacity: 0,
            delay: 0.5,
            stagger: 0.2,
            ease: "elastic",
            force3D: true
        });
    });

    document.querySelector(".gift").addEventListener("click", async function() {
        await gsap.to(".gift", {
            duration: 0.5,
            opacity: 0,
            y: -100,
            stagger: 0.1,
            ease: "back.in"
        });

        $('.gift').css('display','none');
        $('.boxes').css('display','');
        gsap.from(".box", {
            duration: 2,
            scale: 0.5,
            opacity: 0,
            delay: 0.5,
            stagger: 0.2,
            ease: "elastic",
            force3D: true,
            onStart: showMessage,
            onStartParams: ["원하시는 카드번호를 선택해주세요."],
        });
    });

    var output = document.querySelector("#output");

    function showMessage(message) {
        output.innerHTML += message + "</br>";
    }

    // forEach(function(box) {
    //     box.
    // });

    document.querySelectorAll(".box").forEach(function(box, index) {
        box.addEventListener("click", async function() {
            await gsap.to(".box", {
                duration: 0.5,
                opacity: 0,
                y: -100,
                stagger: 0.1,
                ease: "back.in"
            });

            $('.boxes').css('display','none');


            var color = $('.boxes .box').eq(index).attr('class').split(' ')[1];
            $('.prize_box').addClass(color);
            $('.fireworks').css('display','');

            <%--var rand = Number(Math.floor(Math.random()*${fn:length(registrants)}));--%>
            <%--console.log(rand);--%>

<%--            <c:forEach items="${registrants}" var="registrant">--%>
<%--                <c:forEach items="${registrant}" var="item">--%>
<%--                    console.log(${item.name});--%>
<%--                </c:forEach>--%>
<%--            </c:forEach>--%>

            // $('.prize_box > p').html(Number(index)+1);

            var $circle = $(".circle"),
                $inner = $(".inner"),
                $inner1 = $(".circleInner1"),
                $inner2 = $(".circleInner2"),
                $inner3 = $(".circleInner3"),
                $inner4 = $(".circleInner4"),
                $spot = $(".spot"),
                random = Math.floor(Math.random() * 3) + 1;

            running();

            async function running() {
                await burst(0);
                await burst(1);
                await burst(2);

                setTimeout(afterComplete(), 2000);
            }


            function burst(i){
                tl = new TimelineMax({repeat:-1,onCompleteParams:[random]});
                tl
                    .set($inner, { autoAlpha: 0})
                    .set($circle[i], {css: {autoAlpha:1, borderColor:'rgba(27,121,197,0)'}})
                    .from($circle[i], 0.7, {y:+300, ease:Cubic.easeOut})
                    .to($circle[i], 0.8, {y:+5, ease:Cubic.easeIn}, 'Burst')
                    .to($spot, 0.4, { autoAlpha: 1 })
                    .to($spot, 0.2, { autoAlpha: 0 }, '-=0.2')
                    .to($inner1, 0.01, { css: {autoAlpha: 1}}, 'Burst+=0.1')
                    .to($inner2, 0.01, { css: {autoAlpha: 1}}, 'Burst+=0.21')
                    .to($inner3, 0.01, { css: {autoAlpha: 1}}, 'Burst+=0.32')
                    .to($inner4, 0.01, { css: {autoAlpha: 1}}, 'Burst+=0.43')
                    .to($inner1, 1, { scale:1.5, autoAlpha: 0}, 'Burst+=0.13')
                    .to($inner2, 1, { scale:1.5, autoAlpha: 0}, 'Burst+=0.24')
                    .to($inner3, 1, { scale:1.5, autoAlpha: 0}, 'Burst+=0.35')
                    .to($inner4, 1, { scale:1.5, autoAlpha: 0}, 'Burst+=0.46')
                    .set($circle[i], { y:+300} )
                    .set($spot, {autoAlpha: 0})
                ;
                random = randomInteger(0, 3);

            }

            function randomInteger(min, max){
                return Math.floor(Math.random() * (1 + max - min) + min);
            }

            function afterComplete(){

                $('.fireworks').css('display','none');
                $('.prize_box_wrap').css('display','');

                prizeBox();
            }

            async function prizeBox(){
                await gsap.from(".prize_box", {
                    duration: 2,
                    scale: 0.5,
                    opacity: 0,
                    delay: 0.5,
                    stagger: 0.2,
                    ease: "elastic",
                    force3D: true
                });

                $('.prize_box > p').html('<div style="vertical-align: middle;"><br/>' +
                    String(Number(index)+1) + '<br/><br/><br/>' +
                    '<span style="color:white;font-family:cursive;font-size:3em;font-weight:bold;">${registrant.getName()}</span><br/><br/>' +
                    '<span style="color:white;font-family:cursive, Comic Sans, sans-serif;font-size:1.5em;">${registrant.getEmail()}<br/><br/>'+
                    '${registrant.getPhone()}</span>');
            }



        });

    });

    document.querySelector(".prize_box").addEventListener("click", async function() {
        await gsap.to(".prize_box", {
            duration: 0.5,
            opacity: 0,
            y: -100,
            stagger: 0.1,
            ease: "back.in"
        });

        // location.href = location.href;

        $.ajax({
            url: '${baseUrl}/society/${societyAbbr}/conference/${soConfConference.nameId}/prizeLottery/ajax?idx='+${registrant.getId()},
            success:function(data){
                $('.prizeLotteryAjax').html(data);
            }
        })

        // $('.prize_box_wrap').css('display','none');
        // $('.gift').css('display','');

        // gsap.from(".gift", {
        //     duration: 2,
        //     scale: 0.5,
        //     opacity: 0,
        //     delay: 0.5,
        //     stagger: 0.2,
        //     ease: "elastic",
        //     force3D: true
        // });
    });




</script>
<style>
    #fireworks {
        width: 350px;
        margin: 0 auto;
    }
    .circle {
        margin: 0 auto;
        width: 100px;
        height: 100px;
        border: 1px  dotted;
        border-radius: 100%;
        position: relative;
        opacity: 0;
        float: left;
    }
    .inner {
        position: absolute;
        top: 50%;
        left: 50%;
        border: 3px dotted;
        width: 20%;
        height: 20%;
        border-radius: 100%;
        margin: -12% 0 0 -12%;
    }
    .circleInner2 {
        width: 40%;
        height: 40%;
        margin: -21% 0 0 -21%;
        border-width: 2px;
    }
    .circleInner3 {
        width: 70%;
        height: 70%;
        margin: -36% 0 0 -36%;
        border-width: 2px;
        border-color: rgba(27,121,197,0.4);
    }
    .spot {
        width: 5%;
        height: 5%;
        margin: -2% 0 0 -2%;

        position: absolute;
        top: 50%;
        left: 50%;
        border-radius: 100%;
    }
    /* Colors */
    .blue {border-color: rgba(27,121,197,0.2); width: 80px; height: 80px}
    .blue .inner {border-color: rgba(27,121,197,1)}
    .blue .spot {background-color: rgba(27,121,197,0.7)}
    .red {border-color: rgba(197,27,27,0.2)}
    .red .inner {border-color: rgba(197,27,27,1)}
    .red .spot {background-color: rgba(197,27,27,0.7)}
    .green1 {border-color: rgba(135,197,27,0.2); width: 70px; height: 70px}
    .green1 .inner {border-color: rgba(135,197,27,1)}
    .green1 .spot {background-color: rgba(135,197,27,0.7)}
</style>
