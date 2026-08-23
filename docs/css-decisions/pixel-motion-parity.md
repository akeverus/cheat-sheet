# pixel-motion-parity.css — досье решений (ARCH-02)

Полные доводы и история правил, вынесенные из комментариев `pixel-motion-parity.css`
(REF-009): эссе читают при ПЕРЕСМОТРЕ решения, а не при каждом чтении файла.
У правила в CSS остаётся указатель с тезисом; якоря ниже — цель указателей.
Рабочие инварианты («НЕ менять», пороги) остаются в CSS.

<a id="pixel-motion-parity-e01"></a>
## pixel-motion-parity-e01 · pixel-motion-parity.css:1

**Контекст:** `@layer pixel {`

pixel-motion-parity.css — @layer pixel. ВЫНЕСЕНО из pixel-fx.css (Волна 7,
   concern-split, cut 1). Reduced-motion + data-motion=off parity: гасит
   pixel-анимации/декор при prefers-reduced-motion ИЛИ ручном data-motion=off.
   Все правила animation !important — важность бьёт нормальные animation-декларации
   выше по каскаду НЕЗАВИСИМО от порядка/файла, поэтому вынос в отдельный
   @layer pixel файл behavior-neutral.

   Здесь стоял ещё и блок `display: none` для узлов декоративного слоя
   (.pixel-ambient / .pixel-floater / .pixel-trail-dot / .pixel-coin-rain /
   .pixel-screen-flash / .pixel-stage-intro / .pixel-portal-wipe / .pixel-coin-pickup
   и CRT-оверлей `[data-pixel="high"] body::before/::after`). Слой снят целиком —
   гасить стало нечего, и правила ушли вместе с ним.
