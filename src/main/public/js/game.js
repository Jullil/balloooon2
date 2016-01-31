function init_game(canvasElem) {
    var canvas = canvasElem.get(0);
    var canvasWidth = canvasElem.width();
    var canvasHeight = canvasElem.height();

    var canvasCenterX = Math.round(canvasWidth / 2);
    var canvasCenterY = Math.round(canvasHeight / 2);

    Canvas = function (canvas, width, height) {
        this.canvas = canvas;
        this.context = canvas.getContext("2d");
        this.width = width;
        this.height = height;
    };
    $.extend(Canvas, {
        prototype: {
            clear: function () {
                var mainArea = new Path2D();
                mainArea.rect(0, 0, this.width, this.height);
                this.context.clearRect(0, 0, this.width, this.height);
                this.context.stroke(mainArea);
            }
        }
    });

    Location = function (x, y) {
        this.x = x;
        this.y = y;
    };

    Avatar = function (id, location) {
        this.id = id;
        this.location = location;
    };
    $.extend(Avatar, {
        prototype: {
            updateLocation: function (location) {
                this.location = location;
                console.log("Received new location for avatar=" + this.id + ": " + this.location.x + "," + this.location.y);
            },
            repaint: function () {
                var path = new Path2D();
                var radius = 10;
                path.arc(this.location.x, this.location.y, radius, 0, 2 * Math.PI);
                console.log("Repaint avatar: " + this.id);
                return path;
            }
        }
    });

    World = function (canvas, socket) {
        this.canvas = canvas;
        this.avatars = {};
        this.currentAvatar = null;

        var that = this;
        this.canvas.canvas.onmousemove = function (event) {
            var msg = {
                type: "direction",
                data: {
                    x: event.offsetX - that.currentAvatar.location.x,
                    y: event.offsetY - that.currentAvatar.location.y
                }
            };
            socket.send(JSON.stringify(msg));
            console.log(msg)
        };
    };
    $.extend(World, {
        prototype: {
            setAvatar: function (id, location) {
                this.currentAvatar = this.addAvatar(id, location);
            },
            addAvatar: function (id, location) {
                var avatar = new Avatar(id, location);
                this.avatars[avatar.id] = avatar;
                console.log("Added new avatar: " + avatar.id);
                return avatar;
            },
            updateAvatarLocation: function (id, location) {
                var avatar = this.getAvatar(id);
                if (avatar == undefined) {
                    avatar = this.addAvatar(id, location);
                } else {
                    avatar.updateLocation(location);
                }
                this.repaint(avatar);
            },
            getAvatar: function (id) {
                return this.avatars[id];
            },
            repaint: function (avatar) {
                this.canvas.clear();
                this.canvas.context.fill(avatar.repaint());
            }
        }
    });

    function createSocket() {
        var socket = new WebSocket("ws://localhost:9000/game/");

        socket.onmessage = function (event) {
            var msg = $.parseJSON(event.data);
            console.log("Received a message: ");
            console.log(msg);
            switch (msg.type) {
                case "newAvatar" :
                    world.setAvatar(msg.uid, new Location(msg.x, msg.y));
                    break;
                case "location" :
                    world.updateAvatarLocation(msg.uid, new Location(msg.x, msg.y));
                    break;
            }
        };
        return socket;
    }

    function sendLocation(socket, x, y) {
        var msg = {
            type: "location",
            clientId: "uuid_10",
            data: {uid: 0, x: x, y: y}
        };
        socket.send(JSON.stringify(msg));
    }

    var world = new World(new Canvas(canvas, canvasWidth, canvasHeight), createSocket());

}