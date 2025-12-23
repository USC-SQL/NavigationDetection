// creates a circle image object 
// Function arguments.
// radius is the you know ....
// number is the value to display in the circle
// style is this style 
//    style.color   the circle colour. Defaults to  "red";
//    style.font  the font. Defaults to  '18px arial';
//    style.fontColor is the font colour. Defaults to "white";
//    style.fit if true then the number is made to fit te circle as best it can. Defaults to true
//    style.decimals number of decimal places to display. Defaults to 0
// 
// returns 
// Html canvas element with the following custom attributes
//    ctx the 2D context
//    number the value to display
//    radius the radius
//    displayStyle the referance to the style arrgument passed when createCircleNumberImage
//                was called
//    draw the function used to render the image. This function has no arguments 
//        use the displayStyle atrribute to set the style


function createCircleNumberImage(radius,number,style){
    // create HTML 5 image element
    img = document.createElement("canvas");
    // size it
    img.width = Math.ceil(radius * 2);
    img.height = Math.ceil(radius * 2);
    // get a drawing context
    img.ctx = img.getContext("2d");
    
    // set custom attributes
    img.radius = radius;
    img.number = number;
    img.displayStyle = style;
    
    // set defaults
    style.color = style.color ? style.color : "red";
    style.font = style.font ? style.font : '18px arial';
    style.fontColor = style.fontColor ? style.fontColor : "white";
    style.fit = style.fit === undefined ? true : style.fit;
    style.decimals = style.decimals === undefined || isNaN(style.decimals) ? 0 : style.decimals;
    
    // add draw function
    img.draw = function(){
        var fontScale, fontWidth, fontSize, number;
        // resize
        this.width = Math.ceil(this.radius * 2);
        this.height = Math.ceil(this.radius * 2);
        // clear (incase resize did not do it) 
        this.ctx.clearRect(0,0,this.width,this.height);
        
        // draw the circle
        this.ctx.fillStyle = this.displayStyle.color;
        this.ctx.beginPath();
        this.ctx.arc(radius,radius,radius,0,Math.PI * 2);
        this.ctx.fill();
        
        // setup the font styles
        this.ctx.font = this.displayStyle.font;
        this.ctx.textAlign = "center";
        this.ctx.textBaseline = "middle";
        this.ctx.fillStyle = this.displayStyle.fontColor;
        
        // get the value to display
        number = this.number.toFixed(this.displayStyle.decimals);
        
        // get the font size
        fontSize = Number(/[0-9\.]+/.exec(this.ctx.font)[0]);
        if(!this.displayStyle.fit || isNaN(fontSize)){ // Dont fit text or font height unknown
            this.ctx.fillText(number,radius,radius);
        }else{ 
            // fit font as based on the angle from text center to bottom right
            fontWidth = this.ctx.measureText(number).width;
            fontScale = Math.cos(Math.atan(fontSize/fontWidth)) * this.radius * 2 / fontWidth;
            this.ctx.setTransform(fontScale,0,0,fontScale,this.radius,this.radius);
            this.ctx.fillText(number,0,0);
            this.ctx.setTransform(1,0,0,1,0,0); // restor the transform
        }
        
        if(!this.displayStyle.fit || isNaN(fontSize)){ // Dont fit text or font height unknown
             this.ctx.fillText(number,radius,radius);
        }else{ 
             fontScale = Math.cos(Math.atan(fontSize/fontWidth)) * this.radius * 2 / fontWidth;
             this.ctx.setTransform(fontScale,0,0,fontScale,this.radius,this.radius);
             this.ctx.fillText(number,0,0);
             this.ctx.setTransform(1,0,0,1,0,0); // restor the transform
        }
        // return this so you can call the draw function from within a canvas drawImage function
        return this;
    }
    // draw first time
    img.draw()
    // return new image
    return img;
}



var canvas = document.createElement("canvas");
canvas.width = 32;
canvas.height = 32;
canvas.style.position = 'absolute';
canvas.style.left = arguments[0];
canvas.style.top = arguments[1];
canvas.style.zIndex = "99999";
var inputnumber = Number(arguments[2])
var ctx = canvas.getContext("2d");
document.body.appendChild(canvas);



// set comments above the function declaration for help
var sun =  createCircleNumberImage (
    12,1,{
        fontColor : "white",
        font : "8px arial",
        color : "#000000",
        fit : true,
        decimals : 0,
    }
)


function doAgain(){
    //ctx.fillStyle = "black";
    //ctx.fillRect(0,0,canvas.width,canvas.height/2)
    //ctx.fillStyle = "Red";
    //ctx.fillRect(0,canvas.height /2 ,canvas.width,canvas.height/2)

    if(sun.number > 5000000000){
        sun.number = 0;
    }
    sun.number = inputnumber;//sun.number + Math.floor(sun.number/10) + 1;
    ctx.drawImage(sun.draw(),ctx.canvas.width / 2 - sun.radius,ctx.canvas.height / 2 - sun.radius);
    //setTimeout(doAgain,200);
}
doAgain();
