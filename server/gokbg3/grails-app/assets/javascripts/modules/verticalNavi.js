
// modules/verticalNavi.js

verticalNavi = {

    go: function () {
        verticalNavi.init('body')
    },

    init: function () {
        console.log('verticalNavi.init')


        $("#toc").sidebar("setting", "dimPage", false);
        if (matchMedia) {
            console.log("matchMedia")
            var mq = window.matchMedia("(max-width: 992px)");
            mq.addEventListener("change",big_or_small);
            big_or_small(mq);
        }

        function big_or_small(mq) {
            console.log("big_or_small")
            // The sidebar *pushes* the pusher, the main content, so we
            // add a class that reduces the pusher's width so the edge
            // content isn't cut off.
            if (mq.matches) {
                console.log("mq.matches")
                $("#toc").sidebar("hide");
                $("#main").removeClass("shrink")
            } else {
                $("#toc").sidebar("show");
                $("#main").addClass("shrink");
            }
        }

        $("#sidebar-menu-button").click(function() {
            $("#toc").sidebar("toggle");
        });

    }
}