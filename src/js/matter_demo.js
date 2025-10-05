
import Matter from 'matter-js';
    
// module aliases
var Engine = Matter.Engine,
    Render = Matter.Render,
    Runner = Matter.Runner,
    Bodies = Matter.Bodies,
    Composite = Matter.Composite;

function matter_setup() {
    // create an engine
    var engine = Engine.create();
    
    function makeBox(x, y, width, height) {
        return Bodies.rectangle(x, y, width, height);
    }
    
    function addBox(box) {
        Composite.add(engine.world, [box]);
    }
    
    // Create a pyramid of boxes
    function makePyramid(x, y, columns, rowHeight, boxSize) {
        var pyramid = Composite.create({ label: 'Pyramid' });
        var currentX = x;
        
        for (var row = 0; row < rowHeight; row++) {
            var cols = columns - row;
            
            for (var col = 0; col < cols; col++) {

                
                // var box = Bodies.rectangle(
                //     currentX + col * boxSize + row * (boxSize / 2),
                //     y - row * boxSize,
                //     boxSize * 0.9, // Slightly smaller for gaps
                //     boxSize * 0.9,
                //     {
                //         render: {
                //             fillStyle: ['#FFA500', '#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4'][Math.floor(Math.random() * 5)]
                //         }
                //     }
                // );


                var box = Bodies.circle(
                    currentX + col * boxSize + row * (boxSize / 2),
                    y - row * boxSize,
                    boxSize * 0.45, // Radius for circle
                    {
                        render: {
                            fillStyle: "#ffffff"

                            // ['#FFA500', '#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4'][Math.floor(Math.random() * 5)]
                        }
                    }
                );


                
                Composite.add(pyramid, box);
            }
        }
        
        return pyramid;
    }
    
    // Add pyramid to world
    function addPyramid(pyramid) {
        Composite.add(engine.world, pyramid);
    }
    
    // Alternative: create and add pyramid in one step
    function createPyramid(x, y, baseWidth, height) {
        // Calculate box size based on base width
        var boxSize = 40;
        var columns = Math.floor(baseWidth / boxSize);
        var rows = Math.min(columns, height);
        
        var pyramid = makePyramid(x, y, columns, rows, boxSize);
        addPyramid(pyramid);
        return pyramid;
    }
    
    window.addBox = addBox;
    window.makeBox = makeBox;
    window.makePyramid = makePyramid;
    window.addPyramid = addPyramid;
    window.createPyramid = createPyramid;
    
    // create ground

    // addPyramid(pyramid);
    
    // create a renderer
    var render = Render.create({
        element: document.getElementById('matter-canvas'),
        engine: engine,
        options: {
            width: 800,
            height: 600,
            wireframes: false // Show actual colors
        }
    });
    
    // run the renderer
    Render.run(render);
    
    // create runner
    var runner = Runner.create();
    
    // run the engine
    Runner.run(runner, engine);


    function addGround() {

            var ground = Bodies.rectangle(400, 610, 810, 60, { isStatic: true });
    
    // Create a pyramid at position (300, 550) with 10 columns and 10 rows
//    var pyramid = makePyramid(300, 550, 10, 10, 30);
    
    // Add ground and pyramid to world
        Composite.add(engine.world, [ground]);
    }
    

function changeAllBodiesToColor(color) {
    Matter.Composite.allBodies(engine.world).forEach(body => {
        body.render.fillStyle = color;
        body.render.strokeStyle = color;
    });
}


    window.changeAllBodiesToColor = changeAllBodiesToColor
    
    // Optional: Add mouse control for interactions
    var mouse = Matter.Mouse.create(render.canvas);
    var mouseConstraint = Matter.MouseConstraint.create(engine, {
        mouse: mouse,
        constraint: {
            stiffness: 0.2,
            render: {
                visible: false
            }
        }
    });


    
function resetEngine() {
    // Clear the world
    Matter.World.clear(engine.world, false);
    
    // Reset engine timing (optional)
    engine.timing.timestamp = 0;
    
    // Clear any events (optional)
    Matter.Events.off(engine);
    
}


function applyRandomForce(engine, forceMagnitude = 0.05) {
    const bodies = Matter.Composite.allBodies(engine.world)
        .filter(body => !body.isStatic); // Only get non-static bodies
    
    if (bodies.length === 0) return;
    
    // Pick random body
    const randomBody = bodies[Math.floor(Math.random() * bodies.length)];
    
    // Generate random direction (angle in radians)
    const angle = Math.random() * Math.PI * 2;
    
    // Create force vector
    const force = {
        x: Math.cos(angle) * forceMagnitude,
        y: Math.sin(angle) * forceMagnitude
    };
    
    // Apply force at body's center
    Matter.Body.applyForce(randomBody, randomBody.position, force);
    
    return randomBody; // Return the affected body if needed
}


    function setCircleRadius(body, newRadius) {
    // Check if body is a circle (has circleRadius property)
    if (!body.circleRadius) {
        console.warn('Body is not a circle');
        return;
    }
    
    // Store original properties
    const originalPosition = { ...body.position };
    const originalVelocity = { ...body.velocity };
    const originalAngle = body.angle;
    const originalAngularVelocity = body.angularVelocity;
    const renderOptions = { ...body.render };
    
    // Calculate scale factor
    const scaleFactor = newRadius / body.circleRadius;
    
    // Scale the body
    Matter.Body.scale(body, scaleFactor, scaleFactor);
    
    // Restore position and velocities (scaling can affect these)
    Matter.Body.setPosition(body, originalPosition);
    Matter.Body.setVelocity(body, originalVelocity);
    Matter.Body.setAngle(body, originalAngle);
    Matter.Body.setAngularVelocity(body, originalAngularVelocity);
    
    // Update circleRadius property
    body.circleRadius = newRadius;
    
    // Preserve render options
    body.render = renderOptions;
    }

    function setAllCirclesRadius(engine, newRadius) {
    Matter.Composite.allBodies(engine.world).forEach(body => {
        if (body.circleRadius) {
            setCircleRadius(body, newRadius);
        }
    });
}


    window.setCircleRadius = (newRadius) => setCircleRadius(engine, newRadius);
    window.setAllCirclesRadius = (newRadius) => setAllCirclesRadius(engine, newRadius);
    

window.resetEngine = resetEngine;
    window.addGround = addGround;
    
    Composite.add(engine.world, mouseConstraint);
    render.mouse = mouse;


    window.applyRandomForce = function(forceMagnitude) {
        return applyRandomForce(engine, forceMagnitude);
    };


    
}


window.matter_setup = matter_setup;

































