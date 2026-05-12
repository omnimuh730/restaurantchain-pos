ids_amenity = "parking valet wifi credit-cards cash mobile-pay wheelchair high-chairs kids-menu dog-friendly live-music dress-code smoking private-events catering delivery takeout reservations walk-ins outdoor heated-patio ac multilingual bar-lounge".split()
ids_cuisine = "grilled-beef grilled-pork bar-pub meat fine-dining seafood korean western wine brunch vegan steakhouse fusion healthy noodles-soup family-meal".split()
ids_occ = "date-night business-dinner celebration casual-dining romantic family-friendly late-night quick-bite".split()
ids_seat = "dining-hall private-room terrace window-seat bar".split()


def slug_to_en(s: str) -> str:
    t = s.replace("-", " ").title()
    t = t.replace(" Ac ", " A/C ").replace("Wifi", "Wi-Fi")
    t = t.replace("Bar Pub", "Bar & Pub")
    return t


def res_name(s: str) -> str:
    return "amenity_" + s.replace("-", "_")


def build_values(path: str, lang: str) -> None:
    lines = ['<?xml version="1.0" encoding="utf-8"?>', "<resources>"]
    extra = []
    if lang == "en":
        extra = [
            ("settings_amenities_services_title", "Amenities & Services"),
            ("settings_amenities_services_sub", "Toggle features and services your restaurant offers"),
            ("settings_amenities_cuisine_title", "Cuisine"),
            ("settings_amenities_cuisine_sub", "Select the cuisine types your restaurant serves"),
            ("settings_amenities_occasion_title", "Occasion & Vibe"),
            ("settings_amenities_occasion_sub", "Highlight the occasions and vibes your restaurant fits best"),
            ("settings_amenities_seating_title", "Seating Preference"),
            ("settings_amenities_seating_sub", "Seating options available to your guests"),
            ("settings_amenities_active_count", "%1$d active"),
        ]
        label_fn = slug_to_en
    else:
        extra = [
            ("settings_amenities_services_title", "편의시설·서비스"),
            ("settings_amenities_services_sub", "매장에서 제공하는 편의시설을 선택하세요"),
            ("settings_amenities_cuisine_title", "요리 종류"),
            ("settings_amenities_cuisine_sub", "제공하는 요리 스타일을 선택하세요"),
            ("settings_amenities_occasion_title", "분위기·용도"),
            ("settings_amenities_occasion_sub", "어울리는 상황을 강조해 보세요"),
            ("settings_amenities_seating_title", "좌석 유형"),
            ("settings_amenities_seating_sub", "손님이 이용할 수 있는 좌석 옵션"),
            ("settings_amenities_active_count", "선택 %1$d개"),
        ]
        ko = {
            "parking": "주차",
            "valet": "발렛",
            "wifi": "무료 Wi-Fi",
            "credit-cards": "카드 결제",
            "cash": "현금",
            "mobile-pay": "모바일 결제",
            "wheelchair": "휠체어",
            "high-chairs": "유아용 의자",
            "kids-menu": "키즈 메뉴",
            "dog-friendly": "반려동물 동반",
            "live-music": "라이브 음악",
            "dress-code": "드레스 코드",
            "smoking": "흡연 구역",
            "private-events": "돌잔치·행사",
            "catering": "케이터링",
            "delivery": "배달",
            "takeout": "포장",
            "reservations": "예약",
            "walk-ins": "워크인",
            "outdoor": "야외",
            "heated-patio": "난방 테라스",
            "ac": "에어컨",
            "multilingual": "다국어",
            "bar-lounge": "바·라운지",
            "grilled-beef": "소고기 구이",
            "grilled-pork": "돼지고기 구이",
            "bar-pub": "바·펍",
            "meat": "육류",
            "fine-dining": "파인 다이닝",
            "seafood": "해산물",
            "korean": "한식",
            "western": "양식",
            "wine": "와인",
            "brunch": "브런치",
            "vegan": "비건",
            "steakhouse": "스테이크하우스",
            "fusion": "퓨전",
            "healthy": "헬시",
            "noodles-soup": "면·국물 요리",
            "family-meal": "가족 상차림",
            "date-night": "데이트",
            "business-dinner": "비즈니스 만찬",
            "celebration": "축하·파티",
            "casual-dining": "캐주얼 다이닝",
            "romantic": "로맨틱",
            "family-friendly": "가족 친화",
            "late-night": "심야",
            "quick-bite": "가볍게 한 끼",
            "dining-hall": "홀",
            "private-room": "룸",
            "terrace": "테라스",
            "window-seat": "창가석",
            "bar": "바",
        }

        def label_fn(s: str) -> str:
            return ko.get(s, slug_to_en(s))

    for k, v in extra:
        lines.append(f'    <string name="{k}">{v}</string>')
    for s in ids_amenity + ids_cuisine + ids_occ + ids_seat:
        text = label_fn(s)
        lines.append(f'    <string name="{res_name(s)}">{text}</string>')
    lines.append("</resources>")
    with open(path, "w", encoding="utf-8") as f:
        f.write("\n".join(lines) + "\n")


if __name__ == "__main__":
    import os
    root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    build_values(os.path.join(root, "app/src/main/res/values/strings_amenities.xml"), "en")
    build_values(os.path.join(root, "app/src/main/res/values-ko/strings_amenities.xml"), "ko")
    print("ok")
