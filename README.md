# Weher My Home

집 보러 다닐 때 메모하는 앱

## 목차

[1.](#화면-설명) 화면 설명<br>
&nbsp;&nbsp;[1.1.](#1-집-목록-화면) 집 목록 화면<br>
&nbsp;&nbsp;[1.2.](#2-집-상세-화면) 집 상세 화면<br>
&nbsp;&nbsp;[1.3.](#3-사진-전체화면) 사진 전체화면<br>
&nbsp;&nbsp;[1.4.](#4-qa-작성) Q&A 작성<br>
&nbsp;&nbsp;[1.5.](#5-비고-작성-팝업) 비고 작성 팝업<br>
&nbsp;&nbsp;[1.6.](#6-현재-위치-지도) 현재 위치 지도<br>
[2.](#개선할-점) 개선할 점<br>
[3.](#스터디) 스터디<br>
&nbsp;&nbsp;[3.1.](#viewmodel) ViewModel<br>
&nbsp;&nbsp;[3.2.](#navcontroller) NavController<br>
[4.](#license) License<br>

---

## 화면 설명

### 1. 집 목록 화면

- 집 이름/보증금/월세/관리비/Q&A점수 정보를 표시

- 수정(🖍) 버튼을 눌러 항목 수정

- 추가(➕) 버튼을 눌러 새로운 집 정보 추가

  [`MainActivity.kt`](app/src/main/java/com/minuminu/haruu/wheremyhome/view/activity/MainActivity.kt) |
  [`HomeInfoListFragment.kt`](app/src/main/java/com/minuminu/haruu/wheremyhome/view/fragment/HomeInfoListFragment.kt) |
  [`HomeInfoItemRecyclerViewAdapter.kt`](app/src/main/java/com/minuminu/haruu/wheremyhome/view/viewmodel/HomeInfoItemRecyclerViewAdapter.kt)

  ![목록](readme_img/1-home-list.jpg)

### 2. 집 상세 화면

- 카메라(📷) 버튼을 눌러 새로운 사진 찍기

- 사진은 여러 장 추가하여 좌우 스크롤 가능

- 사진을 눌러 전체화면으로 보기

- 지도(🗺) 버튼을 눌러 현재 위치 가져오기

- 뒤로가기(↩) 버튼을 누르면 수정사항이 저장됨

  [`HomeInfoDetailsFragment.kt`](app/src/main/java/com/minuminu/haruu/wheremyhome/view/fragment/HomeInfoDetailsFragment.kt) |
  [`HomeInfoDetailsViewModel.kt`](app/src/main/java/com/minuminu/haruu/wheremyhome/view/viewmodel/HomeInfoDetailsViewModel.kt) |
  [`HomeInfoDetailsBindingAdapter.kt`](app/src/main/java/com/minuminu/haruu/wheremyhome/view/viewmodel/HomeInfoDetailsBindingAdapter.kt)

  ![상세](readme_img/2-home-details.jpg)

### 3. 사진 전체화면

- 뒤로가기(↩)를 누르거나 화면 터치 후 표시되는 '닫기' 버튼을 눌러 나감

  [`PictureFullScreenFragment.kt`](app/src/main/java/com/minuminu/haruu/wheremyhome/view/fragment/PictureFullScreenFragment.kt)

  ![전체화면](readme_img/3-full-screen-img.jpg)

### 4. Q&A 작성

- 입력은 +/-정수와 참/거짓

- 비고(···) 버튼을 눌러 비고 항목 작성하는 팝업 노출

  ![QandA](readme_img/4-home-qanda.png)  

### 5. 비고 작성 팝업

- 작성 후 저장

  [`QandaRemarkDialog.kt`](app/src/main/java/com/minuminu/haruu/wheremyhome/view/dialog/QandaRemarkDialog.kt)

  ![remark](readme_img/5-home-remark.png)

### 6. 현재 위치 지도

- 현재 위치를 표시함

- 뒤로가기를 누르면 현재 위치의 주소를 상세화면의 '위치' 입력란에 저장

  [`MapsFragment.kt`](app/src/main/java/com/minuminu/haruu/wheremyhome/view/fragment/MapsFragment.kt)

  ![map](readme_img/6-home-location-map.png)

## 개선할 점

- Q&A는 블로그에서 찾은 내용으로 26가지 고정 질문임. 질문 추가/수정 화면 필요

- Q&A 점수가 항목별 가중치가 없어 의미없음

- 사진 전체화면에서 '좌우 스와이프' 기능, '사진 저장' 기능 추가 필요

- 지도(🗺) 버튼을 누르면 바로 현재 위치를 찾기 때문에 이전에 입력했던 정보가 지워짐. 지도 화면 내부에 '현재위치 찾기' 버튼을 별도 추가 필요

- 그리고 가장 중요한 실사용 후기... 대충 만들어서 실제 사용해봤는데 방을 금방금방 보다보니 조목조목 체크하기 귀찮아져서 실제 쓸모는 없을 것 같다. 사진, 월세, 위치 저장 기능 정도만 사용.

## 스터디

### ViewModel

- `ViewModel`

```kotlin
class ItemViewModel : ViewModel() {
  /**
   * db 사용할 경우 RoomDatabase 구현체나 Dao를 선언한다.
   *
   * view model과 분리해서 조회 결과만 setter로 넣어줘 '결합도'를 낮출 수 있지만...
   * 귀찮아서 그냥 view model 안에다 선언함
   */
  var db: AppDatabase? = null

  /**
   * Observable (LiveData) 정의
   *
   *     DB/Network -> viewModel
   *
   * 데이터소스(DB, Network 등)로부터 viewModel 값을 받아오는 역할을 한다.
   * LiveData가 값을 받아오면 observer들은 뷰에 값을 갱신하는 작업을 해주면 된다.
   */
  val itemLiveData = MutableLiveData<Item>()

  /**
   * Observable 정의
   *
   *     viewModel -> view
   *         ... or ...
   *     DB/Network -> viewModel -> view
   *
   * ObservableXXX는 값이 변경되면 연결된 layout view를 갱신하는 역할을 한다.
   */
  val name = ObservableField<String>()
  val address = ObservableField<String>()
  val deposit = ObservableField<String>()
  val rental = ObservableField<String>()
  val expense = ObservableField<String>()
  val startDate = ObservableField<String>()
  val endDate = ObservableField<String>()
  val pictureList: ObservableList<DummyContent.Picture> = ObservableArrayList()
  val qandaList: ObservableList<DummyContent.QandaViewData> = ObservableArrayList()

  fun init(db: AppDatabase) {
    this.db = db
  }

  // db에서 id로 item 조회
  fun setItemId(itemId: String) {
    Thread { // item 로드
      db?.homeInfoDao()?.loadAllByIds(arrayOf(itemId))?.takeIf {
        it.isNotEmpty()
      }?.get(0)?.let { homeInfo ->
        itemLiveData.postValue(homeInfo)
      }
    }.start()
  }

  // db에 item 저장/수정
  // ※ suspend : kotlin coroutine 지시어
  suspend fun saveItem(): Item { ... }
}
```

- `View`

```kotlin
class ItemFragment : Fragment() {
  ...
  private var viewModel: ItemViewModel? = null
  private var binding: ItemFragmentBinding? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // viewModel 생성
    viewModel = ViewModelProvider(this).get(HomeInfoDetailsViewModel::class.java).apply {
      init(AppDatabase.getDatabase(requireContext()))
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    /**
     * binding 생성
     *
     * LayoutInflater 대신 DataBindingUtil의 inflate()를 호출하여 view 생성
     */
    binding = DataBindingUtil.inflate(inflater, R.layout.item_fragment, container, false)
    binding?.viewModel = viewModel
    val view = binding?.root

    // 이벤트 등록 등의 작업 수행
    ...view?.findViewById(...).setOnClickListener {...}...

    viewModel?.run {
      // db -> liveData
      itemLiveData.observe(viewLifecycleOwner, { it ->
        // [dataBinding 사용] liveData -> observable -> view 적용
        // - ObservableField의 경우 바인딩된 view에 바로 값 세팅
        name.set(it.name)
        address.set(it.address)
        deposit.set(it.deposit.toString())
        rental.set(it.rental.toString())
        expense.set(it.expense.toString())
        startDate.set(it.startDate)
        endDate.set(it.endDate)

        // - ObservableList의 경우 @BindingAdapter 함수에 직접 바인딩하는 코드를 작성해야함
        for (i in it.pictures.indices) {
          if (i < pictureList.size) {
            pictureList[i] = it.pictures[i]
          } else {
            pictureList.add(it.pictures[i])
          }
        }
        for (i in it.qandas.indices) {
          val qanda: QandaViewData = it.qandas[i].let { qanda ->
            QandaViewData(qanda.id, qanda.group, qanda.num.toString(),
              qanda.question, qanda.type, qanda.answer, qanda.remark)
          }
          if (i < qandaList.size) {
            qandaList[i] = qanda
          } else {
            qandaList.add(qanda)
          }
        }

        // [dataBinding 사용안하는 경우] 뷰에 직접 넣어줘야 한다
        // ...findViewById(...)?.setText(it.name)...
      })
    }

    // 값 세팅
    if (arguments == null) { // [Add Mode]
      viewModel?.itemLiveData?.postValue(createDummyItem())
      viewModel?.pictureList?.addAll(ArrayList())
      viewModel?.qandaList?.addAll(createDummyQandaList())
    } else { // [Edit Mode]
      arguments?.getString("itemId")?.let {
        viewModel?.setItemId(it)
      }
    }

    return view
  }

  fun someFunction() {
    // 뷰 입력값 변경하려면...
    // android:text="@={item.name}"
    viewModel?.name?.set("change it")

    // 뷰 입력값 조회
    // android:text="@{item.name}" // 단방향 바인딩 (사용자가 수정한 값은 조회불가)
    // android:text="@={item.name}" // 양방향 바인딩 (사용자가 수정한 값은 조회가능)
    viewModel?.name?.get()

    // 리스트 항목 추가사항을 뷰에 적용 하려면...
    // [listAdapter 사용 시] <ListView android:adapter=@{pictureListAdapter} ...>
    // [BindingAdapter 사용 시] @BindingAdapter 함수를 정의하여 직접 바인딩
    viewModel?.pictureList?.add(...)
  }
}
```

- `BindingAdapter`

```kotlin
object HomeInfoDetailsBindingAdapter {

  // 사진 추가/삭제 시 호출되어 뷰의 데이터를 갱신한다
  @BindingAdapter("pictures", "layout") // 이곳에 속성을 추가하면 xml에서 바인딩 가능해진다
  @JvmStatic // @BindingAdapter 함수는 static 필수
  fun setPictureList(viewGroup: ViewGroup, // 바인딩 대상 뷰
      pictures: List<Picture>, // @BindingAdapter에 추가한 속성1
      layout: Int) { // @BindingAdapter에 추가한 속성2
    val pictureViewCnt = viewGroup.childCount - 1
    if (pictureViewCnt >= pictures.size)
      return

    val inflater =
      viewGroup.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    for (i in pictures.indices) {
      val picture = pictures[i]

      // [이미 존재하는 사진] 기존 뷰에 데이터만 수정
      if (i < pictureViewCnt) {
        val binding = DataBindingUtil.getBinding<ItemPictureBinding>(viewGroup.getChildAt(i + 1))
        if (binding != null) {
          // binding.picture = picture
          continue
        }
      }

      // [새로 추가된 사진] 새로운 뷰 생성
      DataBindingUtil.inflate<ItemPictureBinding>(inflater, layout, viewGroup, true)?.let {
        it.picture = picture
        it.root.findViewById<ImageView>(R.id.ivPicture)?.setOnClickListener {
          // 전체화면
          viewGroup.findNavController().navigate(
            R.id.action_HomeInfoDetailsFragment_to_PictureFullScreenFragment,
            Bundle().apply {
              putString("pictureName", picture.name)
            })
        }
      }
    }
  }

  @BindingAdapter("pictureName")
  @JvmStatic
  fun setImageBitmap(iv: ImageView, pictureName: String) {
    var imageFile = Utils.loadSnapshotFile(iv.context, pictureName)
    if (imageFile == null) {
      imageFile = Utils.loadImageFile(iv.context, pictureName).let {
        Utils.resizeBitmap(it, iv.width.toFloat(), iv.height.toFloat())
      }
      Utils.createSnapshotFile(iv.context, pictureName, imageFile)
    }
    iv.setImageBitmap(imageFile)
  }
}
```

- `layout`

```xml
...
// ObservableField<String> 바인딩
<com.google.android.material.textfield.TextInputEditText
    android:id="@+id/et_name"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    // android:text="@{viewModel.name}" // 단방향 바인딩
    android:text="@={viewModel.name}"   // 양방향 바인딩 (표현식 사용 시 단방향 바인딩만 가능하다)
    android:ems="255"
    android:hint="@string/name"
    android:inputType="text"
    android:maxLength="255" />
...

// ObservableList<Picture> 바인딩
<LinearLayout
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:pictures="@{viewModel.pictureList}" // @BindingAdapter에 추가한 속성1
    app:layout="@{@layout/item_picture}"    // @BindingAdapter에 추가한 속성2
    android:orientation="horizontal">
```

### NavController

- `Fragment` 간 이동을 `@navigation/nav_graph.xml`에 명시적으로 작성하여 `FragmentTransaction` 보다 쉽고 편하게 관리할 수 있다.

  ![navigation](readme_img/navigation.JPG)

- `@navigation`

```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
  xmlns:app="http://schemas.android.com/apk/res-auto"
  xmlns:tools="http://schemas.android.com/tools"
  android:id="@+id/nav_graph"
  app:startDestination="@id/ItemFragment">

  <fragment
    android:id="@+id/ItemFragment"
    android:name="com.minuminu.haruu.wheremyhome.ItemFragment"
    android:label="@string/item_fragment_label"
    tools:layout="@layout/item_fragment">

    <action
      android:id="@+id/action_ItemFragment_to_PictureFullScreenFragment"
      app:destination="@id/PictureFullScreenFragment" />
    <action
      android:id="@+id/action_ItemFragment_to_MapsFragment"
      app:destination="@id/MapsFragment" />
  </fragment>
  <fragment
    android:id="@+id/PictureFullScreenFragment"
    android:name="com.minuminu.haruu.wheremyhome.PictureFullscreenFragment"
    android:label="전체화면"
    tools:layout="@layout/fragment_picture_fullscreen">
  </fragment>
  <fragment
    android:id="@+id/MapsFragment"
    android:name="com.minuminu.haruu.wheremyhome.MapsFragment"
    android:label="지도"
    tools:layout="@layout/fragment_maps" />
```

- `이동`

```kotlin
import androidx.navigation.fragment.findNavController
...
findNavController().navigate(
  R.id.action_ItemFragment_to_MapsFragment, // @navigation action id
  Bundle().apply {
    putString("address", address)
  })
```

- `이전 Fragment로 이동`

```kotlin
findNavController().popBackStack(R.id.ItemFragment, false)
```

- `SavedState 데이터 변경 리스너`

```kotlin
findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(
  "address"
)?.observe(viewLifecycleOwner, { address ->
  viewModel?.address?.set(address)
})
```

- `Bundle 데이터 변경`

```kotlin
// Notify data changed
findNavController().previousBackStackEntry?.savedStateHandle?.set(
  "address",
  currentMarker?.title
)
```

## License

> 앱 아이콘 제작자 <a href="https://www.flaticon.com/kr/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/kr/" title="Flaticon"> www.flaticon.com</a>

> +/- 아이콘 제작자 <a href="https://www.flaticon.com/authors/dmitri13" title="dmitri13">dmitri13</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
