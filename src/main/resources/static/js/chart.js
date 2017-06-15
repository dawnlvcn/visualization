function drawTreeMap(data, selection, width, height, callback) {
    const treemap = d3.treemap().size([width, height]);
    const root = d3.hierarchy(data, function(d) {
        return d.children;
    }).sum(function(d) {
        return d.level;
    });

    const tree = treemap(root);

    const node = selection.datum(root)
                          .selectAll(".node")
                          .data(tree.leaves())
                          .enter()
                          .append("div")
                          .attr("class", "node")
                          .style("left", function(d) {
                              return d.x0 + "px";
                          })
                          .style("top", function(d) {
                              return d.y0 + "px";
                          })
                          .style("width", function(d) {
                              return Math.max(0, d.x1 - d.x0 - 1) + "px";
                          })
                          .style("height", function(d) {
                              return Math.max(0, d.y1 - d.y0 - 1) + "px";
                          })
                          .style("line-height", function(d) {
                              return Math.max(0, d.y1 - d.y0 - 1) + "px";
                          })
                          .style("vertical-align", "middle")
                          .style("background", function(d) {
                              return color(d.data.name);
                          })
                          .text(function(d) {
                              return d.data.name;
                          })
                          .call(callback);

}

function drawForce(data, selection, width, height, callback) {

    console.log(data);
    selection.selectAll("*").remove();

    var graphSvg = selection.append("svg")
                            .attr("class", "graphSvg")
                            .attr("width", width)
                            .attr("height", height);

    var graphGroup = graphSvg.append("g")
                             .style("left", margin.left + "px")
                             .style("top", margin.top + "px")
                             .call(graphSvgZoom);
    var graphSvgZoom = d3.zoom()
                         .scaleExtent([1, Infinity])
                         .translateExtent([[0, 0], [width, height]])
                         .extent([[0, 0], [width, height]])
                         .on("zoom", function() {
                             graphGroup.attr("transform", d3.event.transform);
                         });

    graphGroup.on("click", function() {
        graphGroup.transition()
                  .duration(750)
                  .call(graphSvgZoom.transform, d3.zoomIdentity);
    });

    graphGroup.append("rect")
              .attr("class", "background")
              .attr("width", width)
              .attr("height", height)
              .style("left", margin.left + "px")
              .style("top", margin.top + "px");

    var simulation = d3.forceSimulation()
                       .force("link", d3.forceLink().id(function(d) {
                           return d.name;
                       }).distance(function distance() {
                           return 30;
                       }))
                       .force("collide", d3.forceCollide(80).iterations(5))
                       .force("charge", d3.forceManyBody()
                                          .strength(function strength() {
                                              return -40;
                                          }))
                       .force("center", d3.forceCenter(width / 2, height / 2));
    var link = graphGroup.append("g")
                         .attr("class", "force-links")
                         .selectAll("line")
                         .data(data.links)
                         .enter()
                         .append("line")
                         .attr("stroke-width", function(d) {
                             return Math.sqrt(d.level);
                         })
                         .attr("stroke", "black");

    var ridus = d3.scaleLinear().domain([10, 1]).range([20, 50]);

    var node = graphGroup.append("g")
                         .attr("class", "force-nodes")
                         .selectAll("circle")
                         .data(data.nodes)
                         .enter()
                         .append("circle")
                         .attr("r", function(d) {
                             return ridus(d.level);
                         })
                         .attr("fill", function(d) {
                             var colorEle = d.aspect.color;
                             if (colorEle !== null) {
                                 if (d[colorEle]) {
                                     return color(d[colorEle]);
                                 } else if (d.otherData[colorEle]) {
                                     return color(d.otherData[colorEle]);
                                 }
                             }
                             return color(d.level);
                         })
                         .call(d3.drag()
                                 .on("start", dragstarted)
                                 .on("drag", dragged)
                                 .on("end", dragended))
                         .call(drawForceCallback);

    var label = graphGroup.selectAll(".force-nodelabel")
                          .data(data.nodes)
                          .enter()
                          .append("text")
                          .attr("class", "force-nodelabel")
                          .text(function(d) {
                              return d.name;
                          })
                          .style("text-anchor", "middle")
                          .style("fill", "#555")
                          .style("font-family", "Arial")
                          .style("font-size", 12);

    node.append("title").text(function(d) {
        var title = "";
        if (!(d.otherData == null)) {
            var titleEles = d.aspect.titles;
            if (titleEles == null) {
                titleEles = d3.keys(d.otherData);
            }
            for (var key in titleEles) {
                title += titleEles[key].toLowerCase() + " : " + d.otherData[titleEles[key]] + "\n";
            }
        } else {
            title = d.name;
        }
        return title;
    });

    simulation.nodes(data.nodes).on("tick", ticked);

    simulation.force("link").links(data.links);

    function ticked() {
        link.attr("x1", function(d) {
            return d.source.x;
        }).attr("y1", function(d) {
            return d.source.y;
        }).attr("x2", function(d) {
            return d.target.x;
        }).attr("y2", function(d) {
            return d.target.y;
        });

        node.attr("cx", function(d) {
            var radius = ridus(d.level);
            return d.x = Math.max(radius, Math.min(width - radius, d.x));
        }).attr("cy", function(d) {
            var radius = ridus(d.level);
            return d.y = Math.max(radius, Math.min(height - radius, d.y));
        });

        label.attr("x", function(d) {
            return d.x;
        }).attr("y", function(d) {
            return d.y;
        });
    }
    function dragstarted(d) {
        if (!d3.event.active)
            simulation.alphaTarget(0.3).restart();
        d.fx = d.x;
        d.fy = d.y;
    }

    function dragged(d) {
        d.fx = d3.event.x;
        d.fy = d3.event.y;
    }

    function dragended(d) {
        if (!d3.event.active)
            simulation.alphaTarget(0);
        d.fx = null;
        d.fy = null;
    }
}

function drawPack(data, selection, width, height, callback) {
    selection.selectAll("*").remove();

    var graphSvg = selection.append("svg")
                            .attr("class", "graphSvg")
                            .attr("width", width)
                            .attr("height", height);

    var pack = d3.pack().size([width, height]).padding(5);

    var rootData = d3.hierarchy(data).sum(function(d) {
        return (10 - (+d.level));
    }).sort(function(a, b) {
        return b.level - a.level;
    });
    console.log(rootData);
    console.log(pack(rootData).descendants());

    var node = graphSvg.selectAll(".packnode")
                       .data(pack(rootData).descendants())
                       .enter()
                       .append("g")
                       .attr("class", "packnode")
                       .attr("transform", function(d) {
                           return "translate(" + d.x + "," + d.y + ")";
                       });
    node.append("circle")
        .attr("r", function(d) {
            return d.r;
        })
        .style("fill", "rgb(31, 119, 180)")
        .style("fill-opacity", "0.25")
        .style("stroke", "rgb(31, 119, 180)")
        .style("stroke-width", "1px");

    node.append("title").text(function(d) {
        return d.data.name + "\n";
    });

    node.filter(function(d) {
        return !d.children;
    }).append("text").attr("dy", "0.3em").text(function(d) {
        return d.data.name.substring(0, d.r / 3);
    }).style("font", "10px sans-serif").style("text-anchor", "middle");
}

function drawTree(data, selection, width, height, callback) {
    console.log(data);
}

function drawPie(data, selection, width, height, callback) {
    console.log(data);
}

function drawPartition(data, selection, width, height, callback) {
    console.log(data);
}

function drawChort(data, selection, width, height, callback) {
    console.log(data);
}

function drawLinechart(data, selection, width, height, callback) {
    console.log(data);
}

function drawBundle(data, selection, width, height, callback) {
    console.log(data);
}

function drawCluster(data, selection, width, height, callback) {
    console.log(data);
}

function drawHistogram(data, selection, width, height, callback) {
    console.log(data);
}
