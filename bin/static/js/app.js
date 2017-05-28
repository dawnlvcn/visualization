var localCache = window.sessionStorage;
var mapCacheMaxSize = null; // TODO just using d3.map to cache data
if (!localCache) {
    console.warn("the browser no support localStorage, no cache data.");
}

const containerWidth = document.body.clientWidth;
// the height of title is 35px
const containerHeight = document.body.clientHeight - 35;
const margin = {
    top    : 35,
    right  : 10,
    bottom : 10,
    left   : 10
};
const width = containerWidth - margin.left - margin.right;
const height = containerHeight - margin.top - margin.bottom;
const color = d3.scaleOrdinal(d3.schemeCategory20c);
const displayTransition = d3.transition().duration(1000).ease(d3.easeCircleIn);

const menuWidth = width * 2 / 3;
const menuHeight = height * 3 / 4;

d3.select(".view-content")
  .style("width", containerWidth + "px")
  .style("height", containerHeight + "px");

const viewMenu = d3.select(".view-menu")
                   .style("width", containerWidth + "px")
                   .style("height", containerHeight + "px")
                   .style("line-height", containerHeight + "px")
                   .style("text-align", "center")
                   .style("vertical-align", "middle")
                   .append("div")
                   .attr("class", "menu-items")
                   .style("position", "relative")
                   .style("display", "inline-block")
                   .style("width", menuWidth + "px")
                   .style("height", menuHeight + "px")
                   .style("top", margin.top + "px");

var viewMenuDisplay = {
    hide : function() {
        viewMenu.transition(displayTransition).style("display", "none");
    },
    show : function() {
        viewMenu.transition(displayTransition).style("display", "inline-block");
    }
}

var graphContainer = d3.select(".graphContainer")
                       .style("width", containerWidth + "px")
                       .style("height", containerHeight + "px")
                       .style("display", "none");

var graphDisplay = {
    hide : function() {
        graphContainer.transition(displayTransition).style("display", "none");
    },
    show : function() {
        if (graphContainer.style("display") == "none") {
            graphContainer.selectAll("*").remove();
            graphContainer.style("display", "inline-block");
        }
    }
}

function loadViewMenu() {
    var userName = d3.select("#username").text();
    var dsName = d3.select("#select-ds").property("value");
    console.log(dsName);
    var ALLVIEWNAMECACHENAME = dsName;
    clearCookie(ALLVIEWNAMECACHENAME); // clear cookie during development

    var viewNames = null;
    if (localCache) {
        console.log("load all view names from local sessionStorage...");
        viewNames = JSON.parse(localCache.getItem(ALLVIEWNAMECACHENAME));
    } else {
        console.log("load all view names from local cookie...");
        viewNames = loadAllViewNamesCookie();
    }

    console.log(viewNames);

    if (viewNames == null) {
        var parameter = "username=" + userName + "&dsname=" + dsName;
        d3.request("/view/allViewNames")
          .header("Content-Type", "application/x-www-form-urlencoded")
          .post(parameter, drawMenu);
    } else {
        drawMenu(null, viewNames);
    }

    function drawMenu(error, result) {
        if (error) {
            throw error;
        }
        var viewNamesJsonData = result;
        if (!(result.responseText === undefined)) {
            // parse data from request result, and cache it
            viewNamesJsonData = JSON.parse(result.responseText);
            if (localCache) {
                console.log("cache all view names to local sessionStorage...");
                localCache.setItem(ALLVIEWNAMECACHENAME, JSON.stringify(viewNamesJsonData));
            } else {
                console.log("cache all view names data to local cookie...");
                setAllViewNamesCookie(ALLVIEWNAMECACHENAME, viewNamesJsonData, 1);
            }
        }
        drawTreeMap(viewNamesJsonData, viewMenu, menuWidth, menuHeight, viewMenuEvent);
    }

    function loadAllViewNamesCookie() {
        var nameArr = null;
        var nameReg = new RegExp("(^| )" + ALLVIEWNAMECACHENAME + "=([^;]*)(;|$)");
        if (nameArr = document.cookie.match(nameReg)) {
            var childrenNameArr = nameArr[2].split(",");
            var childrenArr = [];
            for (var index in childrenNameArr) {
                childrenArr.push({
                    name  : childrenNameArr[index],
                    level : 1
                });
            }
            return {
                name     : "all view names",
                children : childrenArr
            };
        } else {
            return null;
        }
    }

    function setAllViewNamesCookie(name, value, days) {
        var exp = new Date();
        exp.setTime(exp.getTime() + days * 24 * 60 * 60 * 1000);

        var newValue = "";
        var childrenArr = value.children;
        for (var i = 0; i < childrenArr.length; i++) {
            newValue += childrenArr[i].name;
            if (i < childrenArr.length - 1) {
                newValue += ",";
            }
        }
        document.cookie = name + "=" + newValue + ";expires=" + exp.toGMTString();
    }

    function clearCookie(name) {
        var exp = new Date();
        exp.setTime(exp.getTime() - 1 * 24 * 60 * 60 * 1000);
        document.cookie = name + "=" + ";expires=" + exp.toGMTString();
    }
}

function viewMenuEvent(selection) {

    selection.on("mouseover", function(d) {
        var currColor = d3.color(color(d.data.name));
        currColor.opacity = 0.6;
        d3.select(this)
          .style("background", d3.rgb(currColor))
          .style("cursor", "pointer");
    }).on('mouseout', function(d) {
        d3.select(this)
          .style("background", d3.rgb(color(d.data.name)))
          .style("cursor", "pointer");
    }).on("click", function(d) {
        console.log(selection);
        var currColor = d3.color(color(d.data.name));
        currColor.opacity = 1;
        d3.select(this).style("background", d3.rgb(currColor))
        viewMenuDisplay.hide();
        d3.select("#view-home").style("display", "inline-block");
        d3.select("#current-view")
          .attr("value", d3.select(this).text())
          .text("\\ " + d3.select(this).text());
        drawViewGraph(d3.select(this).text());
    });
    d3.select("#view-home").on("click", function() {
        d3.select("#current-view").text("");
        d3.select("#view-home").style("display", "none");
        graphDisplay.hide();
        viewMenuDisplay.show();
    });
}

function drawViewGraph(viewName) {
    var viewData = null;
    if (localCache) {
        viewData = JSON.parse(localCache.getItem(viewName));
    }

    if (viewData != null) {
        drawing(null, viewData);
    } else {
        var userName = d3.select("#username").text();
        var dsName = d3.select("#select-ds").property("value");
        var filterdefault = "false";
        var parameter = "username=" + userName + "&dsname=" + dsName + "&filterdefault=" + filterdefault;
        parameter += "&viewname=" + viewName;
        console.log("fetch data for view:" + viewName);
        d3.request("/view/data")
          .header("Content-Type", "application/x-www-form-urlencoded")
          .post(parameter, drawing);
    }
}

function drawing(error, data) {
    if (error) {
        console.log(error)
        throw error;
    }

    graphDisplay.show();

    var viewJsonData = data;
    if (!(data.responseText === undefined)) {
        // parse data from request result, and cache it
        viewJsonData = JSON.parse(result.responseText);
        if (localCache) {
            localCache.setItem(viewJsonData.viewName, JSON.stringify(viewJsonData));
        }
    }
    const chartType = viewJsonData.chartType;
    switch (chartType) {
        case "TREEMAP" :
            drawTreeMap(viewJsonData.data, graphContainer, width, height, drawTreeMapCallback);
            break;
        case "FORCE" :
            drawForce(sliceForceData(viewJsonData.data), graphContainer, width, height, drawForceCallback);
            break;
        default :
            unSupportChartType(viewJsonData);
    }
    function unSupportChartType(viewData) {
        console.log("unsupport chart type: " + viewData.chartType);
        console.log(viewData);
    }

}

d3.select(".view-content")
  .append("a")
  .attr("class", "next")
  .classed("hover", true)
  .on("click", function() {
      const defautEleSize = 50;
      var curri = +(d3.select("#current-view").attr("data-currentIndex"));
      d3.select("#current-view")
        .attr("data-currentIndex", curri + defautEleSize);
      d3.select("#current-view").attr("next", true);
      pageingDisplay.prevShow();
      drawViewGraph(d3.select("#current-view").attr("value"));
      const allCount = d3.select("#current-view").attr("allCount");
      if ((curri + defautEleSize * 2) > allCount) {
          pageingDisplay.nextHide();
      }

  })
  .text("next");

d3.select(".view-content")
  .append("a")
  .attr("class", "prev")
  .classed("hover", true)
  .on("click", function() {
      var curri = +(d3.select("#current-view").attr("data-currentIndex"));
      const defautEleSize = 50;
      const targetIndex = curri - defautEleSize;
      if (targetIndex >= 0) {
          d3.select("#current-view").attr("data-currentIndex", targetIndex);
          d3.select("#current-view").attr("next", false);
          drawViewGraph(d3.select("#current-view").attr("value"));
          if ((targetIndex - defautEleSize) < 0) {
              pageingDisplay.prevHide();
          }
      }
  })
  .text("prev");

var pageingDisplay = {
    prevHide : function() {
        d3.select(".prev").style("opacity", "0");
    },
    prevShow : function() {
        d3.select(".prev").style("opacity", "1");
    },

    nextHide : function() {
        d3.select(".next").style("opacity", "0");
    },
    nextShow : function() {
        d3.select(".next").style("opacity", "1");
    }
}
function sliceForceData(data) {
    var localData = data;

    d3.select("#current-view").attr("allCount", localData.links.length);

    console.log(data);
    const defautEleSize = 50;
    var currentIndex = d3.select("#current-view").attr("data-currentIndex");
    currentIndex = currentIndex === undefined ? 0 : +currentIndex;
    const next = d3.select("#current-view").attr("next");
    var endIndex;
    console.log(next);
    if (next == null || next) {
        endIndex = currentIndex + defautEleSize;
    } else {
        endIndex = currentIndex - defautEleSize;
        endIndex = endIndex <= 0 ? 0 : endIndex;
    }
    const nodeLength = localData.nodes.length;

    if (nodeLength > endIndex) {
        if (localData.links.length == 0) {
            return localData;
        }
        pageingDisplay.nextShow();
        var newLinks = localData.links.slice(currentIndex, endIndex);
        var keySet = [];
        for (var i in newLinks) {
            keySet.push(newLinks[i].source);
            keySet.push(newLinks[i].target);
        }
        keySet = d3.set(keySet);
        var newNodes = [];
        for (var index in data.nodes) {
            var currentNode = localData.nodes[index];
            if (keySet.has(currentNode.name)) {
                newNodes.push(currentNode);
            }
        }
        var newData = {
            "nodes" : newNodes,
            "links" : newLinks
        };
        return newData;
    } else {
        return localData;
    }
}

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

function drawTreeMapCallback(selection) {

}

function drawForce(data, selection, width, height, callback) {

    console.log(data);
    selection.selectAll("*").remove();

    var graphSvg = selection.append("svg")
                            .attr("class", "graphSvg")
                            .attr("width", width)
                            .attr("height", height);

    var graphSvgZoom = d3.zoom()
                         .scaleExtent([1, Infinity])
                         .translateExtent([[0, 0], [width, height]])
                         .extent([[0, 0], [width, height]])
                         .on("zoom", function() {
                             graphGroup.attr("transform", d3.event.transform);
                         });

    var graphGroup = graphSvg.append("g")
                             .style("left", margin.left + "px")
                             .style("top", margin.top + "px")
                             .call(graphSvgZoom);

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

function drawForceCallback(selection) {

}
