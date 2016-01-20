function init_game(canvasElem) {
    var canvas = canvasElem.get(0);
    var ctx = canvas.getContext("2d");
    var canvasWidth = canvasElem.width();
    var canvasHeight = canvasElem.height();

    var mainArea = new Path2D();
    mainArea.rect(0, 0, canvasWidth, canvasHeight);
    ctx.stroke(mainArea);

    var avatar = new Path2D();
    avatar.arc(100, 100, 25, 0, 2 * Math.PI);

    ctx.fill(avatar);
}