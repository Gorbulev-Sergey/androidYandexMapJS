let map;
let placemark;
let Moscow = [55.76, 37.64];
let Kaluga = [54.513724, 36.263902];
let s = {
  mhbrJ3PLZqb5KGAYuR9G9YnYSGZ2: {
    isClient: false,
    isDriver: true,
    isOnline: true,
    myLocation: {
      latitude: 37.421998333333335,
      longitude: -122.08400000000002,
    },
  },
  sXReVWg0VJUn6h28wGBazIjKy582: {
    isClient: false,
    isDriver: true,
    isOnline: true,
    myLocation: { latitude: 54.39622166666666, longitude: 35.73603333333333 },
  },
  KC5tpz0DC4hMvPGi7I0lMAW4A9X2: {
    isClient: false,
    isDriver: true,
    isOnline: false,
    myLocation: { latitude: 54.508191, longitude: 36.254339 },
  },
  OXEfRTS65lZxcYE5eVA0nRpunAp2: {
    isClient: false,
    isDriver: true,
    isOnline: false,
    myLocation: { latitude: 54.51351, longitude: 36.315028 },
  },
  fIfk0QleuobVhtqWaTttr6DnIIp1: {
    isClient: false,
    isDriver: true,
    isOnline: false,
    myLocation: { latitude: 0.0, longitude: 0.0 },
  },
  "19L6CVq78sVSNr9e4Dl6G9EOdIH2": {
    isClient: false,
    isDriver: true,
    isOnline: false,
    myLocation: { latitude: 54.499055, longitude: 36.289872 },
  },
};

ymaps.ready(function () {
  map = new ymaps.Map("map", {
    center: Kaluga,
    zoom: 14,
  });

  // Скрываем элементы управления
  map.controls.remove("geolocationControl"); // удаляем геолокацию
  map.controls.remove("searchControl"); // удаляем поиск
  map.controls.remove("trafficControl"); // удаляем контроль трафика
  map.controls.remove("typeSelector"); // удаляем тип
  map.controls.remove("fullscreenControl"); // удаляем кнопку перехода в полноэкранный режим
  //map.controls.remove("zoomControl"); // удаляем контрол зуммирования
  map.controls.remove("rulerControl"); // удаляем контрол правил
  map.behaviors.disable(["scrollZoom"]); // отключаем скролл карты (опционально)

  // Размещение геообъекта на карте.
  placemark = new ymaps.Placemark(
    Kaluga,
    {},
    {
      iconLayout: "default#image",
      iconImageHref:
        "https://i.ebayimg.com/00/s/MTAyNFgxMDI0/z/WOgAAOSwVLRaT6J8/$_3.JPG?set_id=8800005007",
      iconImageSize: [40, 40],
      iconImageOffset: [-20, -20],
    }
  );
  addPlacemarkMap(s);
});

function setCoord(lat, lon) {
  placemark.geometry.setCoordinates([lat, lon]); // Смотреть описание здесь https://yandex.ru/dev/maps/jsapi/doc/2.1/dg/concepts/map.html
}

function setCenter(lat, lon) {
  //map.setCenter([lat, lon]);
  map.panTo([lat, lon], { duration: 1500 });
}

function addPlacemark(lat, lon) {
  map.geoObjects.add(
    new ymaps.Placemark(
      [lat, lon],
      {},
      {
        iconLayout: "default#image",
        iconImageHref:
          "https://i.ebayimg.com/00/s/MTAyNFgxMDI0/z/WOgAAOSwVLRaT6J8/$_3.JPG?set_id=8800005007",
        iconImageSize: [40, 40],
        iconImageOffset: [-20, -20],
      }
    )
  );
}

async function addPlacemarkAsync(userUId, lat, lon) {
  let p = new Promise((res, rej) => {
    let balloon = ymaps.templateLayoutFactory.createClass(
      `<div>
              <h5>Водитель ${userUId}</h5>
              <p>Координаты объекта: ${lat}, ${lon}</p>
              <button class="btn btn-sm btn-light mt-3 mr-1" onclick="setCenter(${lat}, ${lon})">В центр</button>
            </div>`
    );
    map.geoObjects.add(
      new ymaps.Placemark(
        [lat, lon],
        {},
        {
          iconLayout: "default#image",
          iconImageHref:
            "https://i.ebayimg.com/00/s/MTAyNFgxMDI0/z/WOgAAOSwVLRaT6J8/$_3.JPG?set_id=8800005007",
          iconImageSize: [40, 40],
          iconImageOffset: [-20, -20],
          balloonContentLayout: balloon,
          balloonPanelMaxMapArea: 0,
        }
      )
    );
  });
  await p;
}

async function addPlacemarkMap(sender) {
  let p = new Promise((res, rej) => {
    let m = new Map(Object.entries(sender));
    m.forEach((value, key) => {
      let balloon = ymaps.templateLayoutFactory.createClass(
        `<div>
              <h5>Водитель ${key}</h5>
              <p>Координаты объекта: ${value.myLocation.latitude}, ${value.myLocation.longitude}</p>
              <button class="btn btn-sm btn-light mt-3 mr-1" onclick="setCenter(${value.myLocation.latitude}, ${value.myLocation.longitude})">В центр</button>
          </div>`
      );
      map.geoObjects.add(
        new ymaps.Placemark(
          [value.myLocation.latitude, value.myLocation.longitude],
          {},
          {
            iconLayout: "default#image",
            iconImageHref:
              "https://i.ebayimg.com/00/s/MTAyNFgxMDI0/z/WOgAAOSwVLRaT6J8/$_3.JPG?set_id=8800005007",
            iconImageSize: [40, 40],
            iconImageOffset: [-20, -20],
            balloonContentLayout: balloon,
            balloonPanelMaxMapArea: 0,
          }
        )
      );
    });
  });
  await p;
}

function removePlacemark() {
  map.geoObjects.remove(placemark);
}
