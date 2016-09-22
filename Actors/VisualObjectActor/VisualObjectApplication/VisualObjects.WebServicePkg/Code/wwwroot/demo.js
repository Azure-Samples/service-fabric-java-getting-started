
var nodeBuffersUpdated = false;
var nodesToRender = new Array();
var triangles = new Array();
var triangleHistories = new Array();
var sizeHistory = new Array();
var historiesCreated = false;

function updateNodeBuffers(nodes) {

    for (var incomingNodes = 0; incomingNodes < nodes.length; ++incomingNodes) {
        var node = nodes[incomingNodes];
        nodesToRender[incomingNodes] = node;

        if(triangles[incomingNodes] == null)
        {
            var t = new Path.RegularPolygon(new Point(0, 0), 3, 20);
            t.fillColor = new Color(node.currentColor.r, node.currentColor.g, node.currentColor.b);     
            triangles[incomingNodes] = t;

            var numHistory = node.history.length;
            triangleHistories[incomingNodes] = new Array();
            sizeHistory[incomingNodes] = 20;
        }
        var numHistory = node.history.length;
        if (triangleHistories[incomingNodes].length == 0 || triangleHistories[incomingNodes].length < numHistory) 
        {
            for(historyEntry = 0; historyEntry < numHistory; ++historyEntry)
            {
                if (triangleHistories[incomingNodes][historyEntry] != null)
                {
                    continue;
                }
                var h = new Path.RegularPolygon(new Point(0, 0), 3, (sizeHistory[incomingNodes] - sizeHistory[incomingNodes]/6));
                sizeHistory[incomingNodes] = sizeHistory[incomingNodes] - sizeHistory[incomingNodes]/6;
                h.fillColor = new Color(node.currentColor.r, node.currentColor.g, node.currentColor.b); 
                h.fillColor.alpha = 1 - (0.11 * (numHistory - historyEntry));
                triangleHistories[incomingNodes][historyEntry] = h;
            }
            

            if(incomingNodes == nodes.length - 1)
                historiesCreated = true;
        }

    }

    nodesToRender = nodes;


    if(historiesCreated)
        nodeBuffersUpdated = true;
}

function drawScene() 
{ 
    if (nodeBuffersUpdated) 
    {
        var numNodes = nodesToRender.length;

        for (nodeToRender = 0; nodeToRender < numNodes; ++nodeToRender) 
        {

            var node = nodesToRender[nodeToRender];
                
            triangles[nodeToRender].position = scalePosToViewport(node.current.x, node.current.y);
            triangles[nodeToRender].rotation = node.rotation;

            var historyCount = triangleHistories[nodeToRender].length;

            for(historyEntry = historyCount - 1, index = 0; historyEntry >= 0; --historyEntry, ++index)
            {
                var historyNodeData = node.history[index];
                var historyTriangle = triangleHistories[nodeToRender][historyEntry];
                historyTriangle.position = scalePosToViewport(historyNodeData.x, historyNodeData.y);
                historyTriangle.rotation = node.rotation;
            }
                    
        }           

        nodeBuffersUpdated = false;
    }
}

function scalePosToViewport(nodex, nodey)
{
    var xfactor = view.viewSize.width / 2;
    var yfactor = view.viewSize.height / 2;

    var xval = nodex + 1;
    var yval = nodey + 1;

    //scaling factor is width or height over 2.
    //2 = width or height, 0 = 0;

    return new Point(xval * xfactor, yval * yfactor);   
}

function startDrawing() {
    var canvas = document.getElementById("canvas");

    canvas.style.border = "#00ff00 3px solid";

    paper.install(window);
    paper.setup('canvas');

    initXmlHttp();

    view.onFrame = function(event) {

        drawScene();
        animate();
    }
}


var lastTime;
var xmlhttp;
var reqPending;
// this should match the interval used in requestAnimFrame()
var pollIntervalMillis = 200;

function initXmlHttp() {
    lastTime = 0;
    xmlhttp = new XMLHttpRequest();
    reqPending = false;

    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState == 4) {
            if (xmlhttp.status == 200) {
                
                var nodes = JSON.parse(xmlhttp.responseText);
                updateNodeBuffers(nodes);
            }
            reqPending = false;
        }
    };
}


function animate() {
    var timeNow = Date.now();

    if (lastTime != 0) {
        var elapsed = timeNow - lastTime;

        if (elapsed > pollIntervalMillis) {
            if (!reqPending) {
                reqPending = true;
                xmlhttp.open("GET", "/nodes.json?cachebreak=" + timeNow, true);
                xmlhttp.send();
            }

            lastTime = timeNow;
        }
    }
    else {
        lastTime = timeNow;
    }
}