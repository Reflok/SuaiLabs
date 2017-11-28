function onClick(node) {
	var parent = node.parentNode;

	var innerList = parent.getElementsByTagName("ol");

	if (innerList[0].style.display != "none") {
		node.innerHTML = "[+]";
		innerList[0].style.display = "none"
	} else {
		node.innerHTML = "[-]";
		innerList[0].style.display = "block"
	}

}

function addView(node) {
	var parent = node.parentNode;
	var elem = document.getElementById("main");
	var outerList = elem.getElementsByTagName("ol");
	var newList = document.createElement("ol");

	var subLists = outerList[0].getElementsByTagName("ol");
	var outerListItems = outerList[0].getElementsByTagName("li");

	for (var i = 0; i < outerListItems.length; i++) {
		if (outerListItems[i].parentNode !== outerList[0]) {
			continue;

		}
		var newLi = document.createElement("li");
		var str = outerListItems[i].innerHTML;
		newLi.appendChild(document.createTextNode(str.slice(0, str.search("<"))));
		//newLi.appendChild(document.createTextNode(subLists[i].innerHTML));
		newLi.appendChild(document.createTextNode(" ("));
		// continue;
		var list = outerListItems[i].getElementsByTagName("ol");
		var innerListItems = list[0].getElementsByTagName("li");

		for (var j = 0; j < innerListItems.length; j++) {
			newLi.appendChild(document.createTextNode(innerListItems[j].innerHTML));

			if  (j < innerListItems.length - 1) {
				newLi.appendChild(document.createTextNode(", "));
			}
		}

		newLi.appendChild(document.createTextNode(")"));
		newList.appendChild(newLi);

	}

	elem.appendChild(newList);


}