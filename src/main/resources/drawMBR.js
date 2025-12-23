
var canv = document.createElement('canvas'); // creates new canvas element
canv.id = 'canvasdummy'; // gives canvas id
canv.height = document.documentElement.clientHeight;
canv.width = document.documentElement.clientWidth;
canv.style.position = 'absolute';
canv.style.left = '0px';
canv.style.top = '0px';
canv.style.zIndex = "99999";


document.body.insertBefore(canv, document.body.firstChild); // adds the canvas to the body element

var canvas1 = document.getElementById('canvasdummy'); //find new canvas we created
var ctx = canvas1.getContext('2d');

var input = arguments[0];
var thickness = arguments[1];
var color = arguments[2];
console.log(input);



arr1 = input.split(";");


var rectXPos = 50;
var rectYPos = 50;
var rectWidth = 100;
var rectHeight = 100;
for(let i = 0; i < arr1.length; i++) {
	console.log(arr1[i]);
	var arr = arr1[i].split(",");
	drawBorder(arr[0], arr[1], arr[2], arr[3], thickness, color)

}
//drawBorder(rectXPos, rectYPos, rectWidth, rectHeight)

//ctx.strokeRect(20, 20, 150, 100);

function drawBorder(xPos, yPos, width, height, thicc, colo)
{
  
  ctx.lineWidth = thicc;
  ctx.strokeStyle = colo;
  ctx.strokeRect(xPos, yPos, width, height);

}


